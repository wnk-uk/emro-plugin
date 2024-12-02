package com.emro.opener;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

import com.intellij.openapi.wm.WindowManager;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import javax.swing.*;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

@Service(Service.Level.PROJECT)
public final class ElementInfoReceiverService {

    private final Project project;
    private final HttpServer server;

    public ElementInfoReceiverService(Project project) throws IOException {
        System.out.println("HTTP Server started on port 8081");

        this.project = project;
        this.server = HttpServer.create(new InetSocketAddress(8081), 0);
        this.server.setExecutor(java.util.concurrent.Executors.newCachedThreadPool()); // 멀티스레드 지원
        // HTTP 요청을 받을 컨텍스트 설정
        server.createContext("/receive-element-info", handleRequest());
        server.start();

        Disposer.register(project, this::stopServer);
    }

    // HTTP 요청 핸들러
    private HttpHandler handleRequest() {
        return exchange -> {
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                handleOptionsRequest(exchange); // OPTIONS 요청 처리
            } else if ("POST".equals(exchange.getRequestMethod())) {
                String requestBody = new String(exchange.getRequestBody().readAllBytes());
                JsonObject jsonObject = JsonParser.parseString(requestBody).getAsJsonObject();
                String fileName = jsonObject.get("tagName").getAsString() + ".html"; // 이 부분은 실제 경로로 변경 필요

                // IntelliJ에서 파일 열기
                ApplicationManager.getApplication().invokeLater(() -> openFileInEditor(fileName));

                String response = "File opened successfully";
                exchange.sendResponseHeaders(200, response.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            }
        };
    }

    private void handleOptionsRequest(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
        exchange.sendResponseHeaders(204, -1); // No Content
    }

    // 서버 종료 메서드
    public void stopServer() {
        if (server != null) {
            server.stop(0);
            System.out.println("HTTP Server stopped.");
        }
    }


    // 파일 열기 메서드
    private void openFileInEditor(String fileName) {
        // 프로젝트의 루트 디렉터리 가져오기
        VirtualFile projectRoot = project.getBaseDir();

        if (projectRoot != null) {
            // 파일을 재귀적으로 검색
            VirtualFile targetFile = findFileByName(projectRoot, fileName);

            if (targetFile != null) {
                FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
                // 파일 열기
                fileEditorManager.openFile(targetFile, true);

                // IntelliJ 창을 최상단으로 올리기
                JFrame frame = WindowManager.getInstance().getFrame(project);
                if (frame != null) {
                    frame.toFront(); // 창을 최상단으로 가져오기
                    frame.requestFocus(); // 포커스 요청
                }


            } else {
                System.err.println("File not found: " + fileName);
            }
        } else {
            System.err.println("Project root is null.");
        }
    }

    /**
     * 특정 디렉터리에서 파일명을 기준으로 파일을 검색합니다.
     *
     * @param directory 검색할 디렉터리
     * @param fileName  찾고자 하는 파일명
     * @return VirtualFile 찾은 파일 객체 (없으면 null)
     */
    private static VirtualFile findFileByName(VirtualFile directory, String fileName) {
        for (VirtualFile child : directory.getChildren()) {
            if (!child.isDirectory() && fileName.equals(child.getName())) {
                return child; // 파일명을 찾으면 반환
            } else if (child.isDirectory()) {
                // 하위 디렉터리 재귀 검색
                VirtualFile result = findFileByName(child, fileName);
                if (result != null) {
                    return result;
                }
            }
        }
        return null; // 파일을 찾지 못한 경우
    }
}
