package com.kabutar.keyfort.ui.service;

import com.kabutar.keyfort.data.entity.Dimension;
import com.kabutar.keyfort.data.repository.DimensionRepo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class UIExtService {

    private final Logger logger = LogManager.getLogger(UIExtService.class);

    @Autowired
    private DimensionRepo dimensionRepo;

    public Mono<List<Dimension>> execute() {
        return dimensionRepo.getAll().collectList();
    }
}
