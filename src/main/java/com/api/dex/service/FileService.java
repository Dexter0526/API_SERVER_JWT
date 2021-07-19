package com.api.dex.service;

import com.api.dex.domain.File;
import com.api.dex.domain.FileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class FileService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private FileRepository fileRepository;

    public File getFileById(long id){
        return fileRepository.getById(id);
    }

}
