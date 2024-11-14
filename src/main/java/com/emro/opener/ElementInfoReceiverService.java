package com.emro.opener;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

import com.jetbrains.exported.JBRApi;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

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
        // HTTP 요청을 받을 컨텍스트 설정
        server.createContext("/receive-element-info", handleRequest());
        server.start();

        Disposer.register(project, this::stopServer);
    }

    // HTTP 요청 핸들러
    private HttpHandler handleRequest() {
        return exchange -> {
            if ("POST".equals(exchange.getRequestMethod())) {
                String requestBody = new String(exchange.getRequestBody().readAllBytes());
                System.out.println("Received data: " + requestBody);


                System.out.println(requestBody);
                // TODO: 요청 데이터에서 필요한 파일 경로 추출
                String filePath = "./OpenerTest.java"; // 이 부분은 실제 경로로 변경 필요

                // IntelliJ에서 파일 열기
                ApplicationManager.getApplication().invokeLater(() -> openFileInEditor(filePath));

                String response = "File opened successfully";
                exchange.sendResponseHeaders(200, response.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            }
        };
    }

    // 서버 종료 메서드
    public void stopServer() {
        if (server != null) {
            server.stop(0);
            System.out.println("HTTP Server stopped.");
        }
    }


    // 파일 열기 메서드
    private void openFileInEditor(String filePath) {
        VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByPath(filePath);
        if (virtualFile != null) {
            FileEditorManager.getInstance(project).openFile(virtualFile, true);
            System.out.println("Opened file: " + filePath);
        } else {
            System.err.println("File not found: " + filePath);
        }
    }
}
