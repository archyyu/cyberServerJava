package com.cybercafe.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

@Service
public class ZeroMqService {

    public static final String CASHIER_SUBJECT = "wjcsh";
    public static final String CLIENT_SUBJECT = "wjclt";

    private static final Logger log = LoggerFactory.getLogger(ZeroMqService.class);

    private ZContext ctx;
    private ZMQ.Socket publish;

    @PostConstruct
    public void init() {
        try {
            this.ctx = new ZContext();
            this.publish = ctx.createSocket(SocketType.PUB);
            this.publish.setLinger(0);
            this.publish.bind("tcp://*:18002");
            this.publish.setReconnectIVL(20);
            log.info("ZeroMQ Publisher initialized on tcp://*:18002");
        } catch (Exception ex) {
            log.error("Zero MQ init error", ex);
        }
    }

    @PreDestroy
    public void cleanup() {
        if (this.publish != null) {
            this.publish.close();
        }
        if (this.ctx != null) {
            this.ctx.close();
        }
        log.info("ZeroMQ Publisher destroyed");
    }

    public void publish(byte[] msg) {
        try {
            if (this.publish == null) {
                return;
            }
            this.publish.send(msg, 0);
        } catch (Exception ex) {
            log.error("Zero MQ publish error", ex);
        }
    }
}
