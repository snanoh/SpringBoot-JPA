package com.example.study.controller.api;

import com.example.study.ifs.CrudInterface;
import com.example.study.model.network.Header;
import com.example.study.model.network.request.AdminUserApiRequest;
import com.example.study.model.network.response.AdminUserApiResponse;
import org.springframework.web.bind.annotation.*;

public class AdminUserApiController implements CrudInterface<AdminUserApiRequest, AdminUserApiResponse> {


    @Override
    @PostMapping("")
    public Header<AdminUserApiResponse> create(@RequestBody Header<AdminUserApiRequest> request) {
        return null;
    }

    @Override
    @GetMapping("{id}")
    public Header<AdminUserApiResponse> read(Long id) {
        return null;
    }

    @Override
    @PutMapping("")
    public Header<AdminUserApiResponse> update(@RequestBody Header<AdminUserApiRequest> request) {
        return null;
    }

    @Override
    @DeleteMapping("{id}")
    public Header delete(Long id) {
        return null;
    }
}
