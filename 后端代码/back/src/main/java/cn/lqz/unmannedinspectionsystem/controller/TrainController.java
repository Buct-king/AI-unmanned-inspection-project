package cn.lqz.unmannedinspectionsystem.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.io.*;
import java.nio.file.*;
import java.util.*;

@RestController
public class TrainController {

    @PostMapping("/upload_dataset")
    public ResponseEntity<?> uploadDataset(@RequestParam("dataset_zip") MultipartFile datasetZip) {
        try {
            String projectRoot = System.getProperty("user.dir");
            String uploadDir = projectRoot + "/uploads";
            Files.createDirectories(Paths.get(uploadDir));
            String filename = datasetZip.getOriginalFilename();
            String savePath = uploadDir + "/" + filename;
            datasetZip.transferTo(new File(savePath));
            return ResponseEntity.ok(Map.of("success", true, "filename", filename, "save_path", savePath));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/train_yolo")
    public ResponseEntity<?> trainYolo(
            @RequestParam("zip_filename") String zipFilename,
            @RequestParam(value = "exp_name", required = false) String expName,
            @RequestParam(value = "epochs", required = false, defaultValue = "50") int epochs,
            @RequestParam(value = "batch_size", required = false, defaultValue = "16") int batchSize,
            @RequestParam(value = "img_size", required = false, defaultValue = "640") int imgSize,
            @RequestParam(value = "model_cfg", required = false, defaultValue = "yolov5s.yaml") String modelCfg,
            @RequestParam(value = "pretrained_weights", required = false, defaultValue = "yolov5s.pt") String pretrainedWeights
    ) {
        try {
            String projectRoot = System.getProperty("user.dir");
            // yolov5-master 和 back 同级
            String yolov5Root = Paths.get(projectRoot).getParent().resolve("yolov5-master").toString();
            String uploadDir = projectRoot + "/uploads";
            String zipPath = uploadDir + "/" + zipFilename;
            if (!Files.exists(Paths.get(zipPath))) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "zip文件不存在"));
            }
            if (expName == null || expName.isEmpty()) {
                expName = "exp_" + System.currentTimeMillis();
            }
            // 解压到yolov5-master/datasets/exp_xxx/
            String saveDir = yolov5Root + "/datasets/" + expName;
            Files.createDirectories(Paths.get(saveDir));
            // 解压
            unzip(zipPath, saveDir);
            String dataYaml = saveDir + "/data.yaml";
            if (!Files.exists(Paths.get(dataYaml))) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "data.yaml未找到"));
            }
            // 调用python训练
            String trainPyPath = yolov5Root + "/train.py";
            String pythonExe = yolov5Root + "/venv/Scripts/python.exe";
            List<String> cmd = Arrays.asList(
                pythonExe, trainPyPath,
                "--img", String.valueOf(imgSize),
                "--batch", String.valueOf(batchSize),
                "--epochs", String.valueOf(epochs),
                "--data", dataYaml,
                "--cfg", modelCfg,
                "--weights", pretrainedWeights,
                "--name", expName
            );
            ProcessBuilder pb = new ProcessBuilder(cmd);
            pb.redirectErrorStream(true);
            Process process = pb.start();
            StringBuilder log = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    log.append(line).append("\n");
                }
            }
            // 设置超时时间为30分钟（1800秒）
            boolean finished = process.waitFor(1800, java.util.concurrent.TimeUnit.SECONDS);
            int exitCode = finished ? process.exitValue() : -1;
            if (!finished) {
                process.destroyForcibly();
                Map<String, Object> resp = new HashMap<>();
                resp.put("success", false);
                resp.put("train_log", log.toString());
                resp.put("error", "训练超时，已强制终止");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
            }
            if (exitCode != 0) {
                Map<String, Object> resp = new HashMap<>();
                resp.put("success", false);
                resp.put("train_log", log.toString());
                resp.put("error", "训练脚本执行失败，exitCode=" + exitCode);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
            }
            // 获取runs/train/下最新的以exp_name开头的目录
            final String expNameFinal = expName;
            String runsTrainDir = yolov5Root + "/runs/train/";
            File baseDir = new File(runsTrainDir);
            System.out.println("查找目录: " + runsTrainDir);
            System.out.println("exp_name: " + expNameFinal);
            if (baseDir.exists()) {
                File[] allDirs = baseDir.listFiles(File::isDirectory);
                if (allDirs != null) {
                    for (File f : allDirs) {
                        System.out.println("实际存在目录: [" + f.getName() + "]");
                    }
                }
            }
            File[] candidates = baseDir.listFiles((dir, name) -> name.equals(expNameFinal) || name.startsWith(expNameFinal));
            if (candidates != null) {
                for (File f : candidates) {
                    System.out.println("候选目录: [" + f.getName() + "]");
                }
            }
            File latestDir = null;
            long latestTime = 0;
            if (candidates != null) {
                for (File f : candidates) {
                    if (f.isDirectory() && f.lastModified() > latestTime) {
                        latestTime = f.lastModified();
                        latestDir = f;
                    }
                }
            }
            if (latestDir == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "未找到训练结果目录"));
            }
            String resultDir = latestDir.getAbsolutePath() + "/";
            // 读取结果图片（多个，包括val相关）
            String[] imageNames = {"results.png", "confusion_matrix.png", "F1_curve.png", "P_curve.png", "R_curve.png", "PR_curve.png", "val_batch0_labels.jpg", "val_batch0_pred.jpg"};
            List<Map<String, String>> resultImages = new ArrayList<>();
            for (String imgName : imageNames) {
                Path imgPath = Paths.get(resultDir + imgName);
                if (Files.exists(imgPath)) {
                    byte[] imgBytes = Files.readAllBytes(imgPath);
                    String base64 = "data:image/png;base64," + Base64.getEncoder().encodeToString(imgBytes);
                    Map<String, String> imgMap = new HashMap<>();
                    imgMap.put("name", imgName);
                    imgMap.put("base64", base64);
                    resultImages.add(imgMap);
                }
            }
            // 读取主要指标（results.csv），自动适配表头，取每列最大值
            Map<String, Double> metrics = new HashMap<>();
            Path csvPath = Paths.get(resultDir + "results.csv");
            if (Files.exists(csvPath)) {
                try (BufferedReader br = Files.newBufferedReader(csvPath)) {
                    String line;
                    String[] headers = null;
                    List<String[]> rows = new ArrayList<>();
                    while ((line = br.readLine()) != null) {
                        if (headers == null) {
                            headers = line.split(",");
                        } else {
                            rows.add(line.split(","));
                        }
                    }
                    if (headers != null && !rows.isEmpty()) {
                        for (int i = 0; i < headers.length; i++) {
                            String key = headers[i].trim();
                            double maxVal = Double.NEGATIVE_INFINITY;
                            boolean isNumeric = false;
                            for (String[] row : rows) {
                                if (i < row.length) {
                                    try {
                                        double v = Double.parseDouble(row[i].trim());
                                        isNumeric = true;
                                        if (v > maxVal) maxVal = v;
                                    } catch (Exception ignore) {}
                                }
                            }
                            if (isNumeric && maxVal > Double.NEGATIVE_INFINITY) {
                                metrics.put(key, maxVal);
                            }
                        }
                    }
                } catch (Exception ignore) {}
            }
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", true);
            resp.put("exp_name", expName);
            resp.put("real_exp_name", latestDir.getName());
            resp.put("result_images", resultImages);
            resp.put("train_log", log.toString());
            resp.put("metrics", metrics);
            resp.put("model_download_url", "/api/download_model?exp_name=" + latestDir.getName());
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/download_model")
    public ResponseEntity<?> downloadModel(@RequestParam("exp_name") String expName) throws IOException {
        String projectRoot = System.getProperty("user.dir");
        // yolov5-master 和 back 同级
        String yolov5Root = Paths.get(projectRoot).getParent().resolve("yolov5-master").toString();
        String modelPath = yolov5Root + "/runs/train/" + expName + "/weights/best.pt";
        Path path = Paths.get(modelPath);
        if (!Files.exists(path)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "模型文件不存在"));
        }
        byte[] fileBytes = Files.readAllBytes(path);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=best.pt")
                .body(fileBytes);
    }

    @GetMapping("/train_result_image")
    public ResponseEntity<?> trainResultImage(@RequestParam("exp_name") String expName) throws IOException {
        String projectRoot = System.getProperty("user.dir");
        // yolov5-master 和 back 同级
        String yolov5Root = Paths.get(projectRoot).getParent().resolve("yolov5-master").toString();
        String imgPath = yolov5Root + "/runs/train/" + expName + "/results.png";
        Path path = Paths.get(imgPath);
        if (!Files.exists(path)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "结果图片不存在"));
        }
        byte[] imgBytes = Files.readAllBytes(path);
        return ResponseEntity.ok()
                .header("Content-Type", "image/png")
                .body(imgBytes);
    }

    @GetMapping("/list_datasets")
    public ResponseEntity<?> listDatasets() {
        try {
            String projectRoot = System.getProperty("user.dir");
            String uploadDir = projectRoot + "/uploads";
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                return ResponseEntity.ok(Map.of("datasets", List.of()));
            }
            String[] files = dir.list((d, name) -> name.toLowerCase().endsWith(".zip"));
            return ResponseEntity.ok(Map.of("datasets", files != null ? List.of(files) : List.of()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    // 工具方法：解压zip
    private void unzip(String zipFilePath, String destDir) throws IOException {
        try (java.util.zip.ZipInputStream zis = new java.util.zip.ZipInputStream(new FileInputStream(zipFilePath))) {
            java.util.zip.ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                File newFile = new File(destDir, entry.getName());
                if (entry.isDirectory()) {
                    newFile.mkdirs();
                } else {
                    new File(newFile.getParent()).mkdirs();
                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
            }
        }
    }
} 