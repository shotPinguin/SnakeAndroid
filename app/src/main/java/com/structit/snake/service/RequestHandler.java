package com.structit.snake.service;

import org.w3c.dom.Document;

//interface qui joue le  role de callback
public interface RequestHandler {

    void onResponse(Document document);
}
