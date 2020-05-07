package com.example.study.controller;


import com.example.study.model.SearchParam;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class PostController {

    //HTML <form>
    //ajax 검색
    //http post body -> data
    //json, xml, multipart-form(파일) / text-plain
    //

    //@PostMapping(value = "/postMethod", produces = {application-json})
    @PostMapping("/postMethod")
    public SearchParam postMethod(@RequestBody SearchParam serachparam){

        return serachparam;
    }

    @PutMapping("/putMethod")
    public void put(){

    }


    @PatchMapping("/patchMethod")
    public void patch(){

    }
}
