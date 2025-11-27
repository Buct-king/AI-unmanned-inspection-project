package cn.lqz.unmannedinspectionsystem.controller;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/video")
public class VideoController {
    // 原始视频目录
    private final String rawVideoDir = new File(System.getProperty("user.dir")).getParent() + "/yolov5-master/raw_videos";
    // 检测后视频目录
    private final String detectVideoDir = new File(System.getProperty("user.dir")).getParent() + "/yolov5-master/detect_videos";

    @GetMapping("/raw/list")
    public List<String> listRawVideos() {
        File dir = new File(rawVideoDir);
        if (!dir.exists()) return Collections.emptyList();
        return Arrays.stream(dir.listFiles((d, name) -> name.endsWith(".mp4")))
                .map(File::getName).sorted().collect(Collectors.toList());
    }

    @GetMapping("/detect/list")
    public List<String> listDetectVideos() {
        File dir = new File(detectVideoDir);
        if (!dir.exists()) return Collections.emptyList();
        return Arrays.stream(dir.listFiles((d, name) -> name.endsWith(".mp4")))
                .map(File::getName).sorted().collect(Collectors.toList());
    }

    @GetMapping("/raw/{filename}")
    public ResponseEntity<Resource> downloadRaw(@PathVariable String filename) throws IOException {
        File file = new File(rawVideoDir, filename);
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + filename)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @GetMapping("/detect/{filename}")
    public ResponseEntity<Resource> downloadDetect(@PathVariable String filename) throws IOException {
        File file = new File(detectVideoDir, filename);
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + filename)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
} 