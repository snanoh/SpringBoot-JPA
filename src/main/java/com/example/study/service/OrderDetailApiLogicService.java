package com.example.study.service;

import com.example.study.ifs.CrudInterface;
import com.example.study.model.entity.OrderDetail;
import com.example.study.model.network.Header;
import com.example.study.model.network.request.OrderDetailApiRequest;
import com.example.study.model.network.response.OrderDetailApiResponse;
import com.example.study.repository.ItemRepository;
import com.example.study.repository.OrderDetailRepository;
import com.example.study.repository.OrderGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailApiLogicService implements CrudInterface<OrderDetailApiRequest, OrderDetailApiResponse> {

    @Autowired
    OrderDetailRepository orderDetailRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    OrderGroupRepository orderGroupRepository;


    @Override
    public Header<OrderDetailApiResponse> create(Header<OrderDetailApiRequest> request) {
        OrderDetailApiRequest body = request.getData();


        OrderDetail orderDetail = OrderDetail.builder()
                .status(body.getStatus())
                .arrivalDate(body.getArrivalDate())
                .quantity(body.getQuantity())
                .totalPrice(body.getTotalPrice())
                .item(itemRepository.getOne(body.getItemId()))
                .orderGroup(orderGroupRepository.getOne(body.getOrderGroupId()))
                .build();

        OrderDetail newOrderDetail = orderDetailRepository.save(orderDetail);


        return response(newOrderDetail);
    }

    @Override
    public Header<OrderDetailApiResponse> read(Long id) {
        return orderDetailRepository.findById(id)
                .map(orderDetail -> response(orderDetail))
                .orElseGet(()->Header.ERROR("데이터 없음"));
    }

    @Override
    public Header<OrderDetailApiResponse> update(Header<OrderDetailApiRequest> request) {
        OrderDetailApiRequest body = request.getData();

        return orderDetailRepository.findById(body.getId())
                .map(orderDetail -> {
                    orderDetail.setStatus(body.getStatus())
                            .setArrivalDate(body.getArrivalDate())
                            .setTotalPrice(body.getTotalPrice())
                            .setQuantity(body.getQuantity())
                            .setItem(itemRepository.getOne(body.getItemId()))
                            .setOrderGroup(orderGroupRepository.getOne(body.getOrderGroupId()))
                            ;
                    return orderDetail;
                })
                .map(newOrderDetail-> orderDetailRepository.save(newOrderDetail))
                .map(updateOrderDetail->response(updateOrderDetail))
                .orElseGet(()->Header.ERROR("데이터 없음"));
    }

    @Override
    public Header delete(Long id) {
        return orderDetailRepository.findById(id)
                .map(orderDetail -> {
                    orderDetailRepository.delete(orderDetail);
                            return Header.OK();
                })
                .orElseGet(()->Header.ERROR("데이터 없음"));
    }

    private Header<OrderDetailApiResponse> response(OrderDetail newOrderDetail) {
        OrderDetailApiResponse body = OrderDetailApiResponse.builder()
                .id(newOrderDetail.getId())
                .status(newOrderDetail.getStatus())
                .arrivalDate(newOrderDetail.getArrivalDate())
                .quantity(newOrderDetail.getQuantity())
                .totalPrice(newOrderDetail.getTotalPrice())
                .itemId(newOrderDetail.getItem().getId())
                .orderGroupId(newOrderDetail.getOrderGroup().getId())
                .build();

        return Header.OK(body);

    }

}
