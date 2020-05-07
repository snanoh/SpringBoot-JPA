package com.example.study.service;


import com.example.study.ifs.CrudInterface;
import com.example.study.model.entity.Category;
import com.example.study.model.network.Header;
import com.example.study.model.network.request.CategoryApiRequest;
import com.example.study.model.network.response.CategoryApiResponse;
import com.example.study.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryApiLogicService implements CrudInterface<CategoryApiRequest, CategoryApiResponse> {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public Header<CategoryApiResponse> create(Header<CategoryApiRequest> request) {
        CategoryApiRequest body = request.getData();

        Category category = Category.builder()
                .title(body.getTitle())
                .type(body.getType())
                .build();

        Category newCategory = categoryRepository.save(category);

        return response(newCategory);
    }



    @Override
    public Header<CategoryApiResponse> read(Long id) {
        return categoryRepository.findById(id)
                .map(category -> response(category))
                .orElseGet(() -> Header.ERROR("데이터 없음"));
    }

    @Override
    public Header<CategoryApiResponse> update(Header<CategoryApiRequest> request) {
        CategoryApiRequest body = request.getData();

        categoryRepository.findById(body.getId())
                .map(category -> {
                    category.setTitle(body.getTitle())
                            .setType(body.getType())
                            ;
                    return category;

                })
                .map(category -> categoryRepository.save(category))
                .map(category -> response(category))
                .orElseGet(() -> Header.ERROR("데이터 없음"));


        return null;
    }

    @Override
    public Header delete(Long id) {
        return categoryRepository.findById(id)
                .map(category -> {
                        categoryRepository.delete(category);
                        return response(category);
                })
                .orElseGet(() -> Header.ERROR("데이터 없음"));

    }

    private Header<CategoryApiResponse> response(Category category) {
        CategoryApiResponse body = CategoryApiResponse.builder()
                .id(category.getId())
                .title(category.getTitle())
                .type(category.getType())
                .build();

        return Header.OK(body);

    }
}
