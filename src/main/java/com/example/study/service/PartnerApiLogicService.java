package com.example.study.service;


import com.example.study.model.entity.Partner;
import com.example.study.model.network.Header;
import com.example.study.model.network.request.PartnerApiRequest;
import com.example.study.model.network.response.PartnerApiResponse;
import com.example.study.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PartnerApiLogicService extends BaseService<PartnerApiRequest, PartnerApiResponse,Partner> {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public Header<PartnerApiResponse> create(Header<PartnerApiRequest> request) {
        PartnerApiRequest body = request.getData();

        Partner newPartner = Partner.builder()
                .partnerNumber(body.getPartnerNumber())
                .address(body.getAddress())
                .businessNumber(body.getBusinessNumber())
                .callCenter(body.getCallCenter())
                .ceoName(body.getCeoName())
                .name(body.getName())
                .registeredAt(body.getRegisteredAt())
                .category(categoryRepository.getOne(body.getCategoryId()))
                .status(body.getStatus())
                .build();

        Partner createPartner = baseRepository.save(newPartner);

        return reponse(createPartner);
    }

    @Override
    public Header<PartnerApiResponse> read(Long id) {
        return baseRepository.findById(id)
        .map(partner -> reponse(partner))
        .orElseGet(() -> Header.ERROR("데이터 없음"));
    }

    @Override
    public Header<PartnerApiResponse> update(Header<PartnerApiRequest> request) {
        PartnerApiRequest body = request.getData();

        return baseRepository.findById(body.getId())
                .map(partner -> {
                                partner.setCeoName(body.getCeoName())
                                        .setName(body.getName())
                                        .setPartnerNumber(body.getPartnerNumber())
                                        .setBusinessNumber(body.getBusinessNumber())
                                        .setStatus(body.getStatus())
                                        .setRegisteredAt(body.getRegisteredAt())
                                        .setAddress(body.getAddress())
                                        .setCategory(categoryRepository.getOne(body.getCategoryId()))
                                        .setUnregisteredAt(body.getUnregisteredAt())
                                        ;

                        return partner;
                })
                .map(updatePartner -> baseRepository.save(updatePartner))
                .map(newPartner -> reponse(newPartner))
                .orElseGet(() -> Header.ERROR("데이터 없음"));
    }

    @Override
    public Header delete(Long id) {
        return baseRepository.findById(id)
                .map(partner -> {baseRepository.delete(partner);
                                return reponse(partner);
                })
                .orElseGet(() -> Header.ERROR("데이터 없음"));
    }

    private Header<PartnerApiResponse> reponse(Partner partner) {
        PartnerApiResponse body = PartnerApiResponse.builder()
                .id(partner.getId())
                .address(partner.getAddress())
                .businessNumber(partner.getBusinessNumber())
                .callCenter(partner.getCallCenter())
                .ceoName(partner.getCeoName())
                .name(partner.getName())
                .registeredAt(partner.getRegisteredAt())
                .unregisteredAt(partner.getUnregisteredAt())
                .partnerNumber(partner.getPartnerNumber())
                .status(partner.getStatus())
                .categoryId(partner.getCategory().getId())
                .build();

        return Header.OK(body);
    }
}
