package com.example.study.service;


import com.example.study.ifs.CrudInterface;
import com.example.study.model.entity.OrderGroup;
import com.example.study.model.entity.User;
import com.example.study.model.enumclass.UserStatus;
import com.example.study.model.network.Header;
import com.example.study.model.network.request.UserApiRequest;
import com.example.study.model.network.response.ItemApiResponse;
import com.example.study.model.network.response.OrderGroupApiResponse;
import com.example.study.model.network.response.UserApiResponse;
import com.example.study.model.network.response.UserOrderInfoApiResponse;
import com.example.study.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserApiLogicService implements CrudInterface<UserApiRequest, UserApiResponse> {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderGroupApiLogicService orderGroupApiLogicService;

    @Autowired
    private ItemApiLogicService itemApiLogicService;


    // 1. request Data
    // 2. user 생성
    // 3. 생성된 데이터 -> UserApiResponse Return
    @Override
    public Header<UserApiResponse> create(Header<UserApiRequest> request) {

        //1. request Data
        UserApiRequest userApiRequest = request.getData();

        //2. user 생성

        User user = User.builder()
                .account(userApiRequest.getAccount())
                .password(userApiRequest.getPassword())
                .status(UserStatus.REGISTERED)
                .phoneNumber(userApiRequest.getPhoneNumber())
                .email(userApiRequest.getEmail())
                .registeredAt(LocalDateTime.now())
                .build();
        User newUser = userRepository.save(user);

        //3. 생성된 데이터 -> UserApiResponse Return

        return Header.OK(response(newUser));
    }

    @Override
    public Header<UserApiResponse> read(Long id) {

        return userRepository.findById(id)
                        .map(user->response(user))
                        .map(Header::OK)
                        .orElseGet(
                                ()->Header.ERROR("데이터 없음")
                        );

    }

    @Override
    public Header<UserApiResponse> update(Header<UserApiRequest> request) {

        //1. data
        UserApiRequest userApiRequest = request.getData();

        //2. id->user 데이터를 찾고
        Optional<User> optional = userRepository.findById(userApiRequest.getId());

        return optional.map(user ->{
            //3. update
                user.setAccount(userApiRequest.getAccount())
                        .setPassword(userApiRequest.getPassword())
                        .setStatus(userApiRequest.getStatus())
                        .setPhoneNumber(userApiRequest.getPhoneNumber())
                        .setEmail(userApiRequest.getEmail())
                        .setRegisteredAt(userApiRequest.getRegisteredAt())
                        .setUnregisteredAt(userApiRequest.getUnregisteredAt())
                        ;

                return user;

            //4 userApiResponse

         })
          .map(user-> userRepository.save(user)) //update
          .map(updateUser-> response(updateUser))             //userApiResponse
          .map(Header::OK)
          .orElseGet(()->Header.ERROR("데이터 없음"));
    }

    @Override
    public Header delete(Long id) {
        //id -> repository -> user
        Optional<User> optional = userRepository.findById(id);


        //repository ->delete

        return optional.map(user->{
            userRepository.delete(user);

            return Header.OK();
        })
        .orElseGet(()->Header.ERROR("데이터 없음"));

    }

    private UserApiResponse response(User user){
        //user -> userApiResponse
        UserApiResponse userApiResponse = UserApiResponse.builder()
                .id(user.getId())
                .account(user.getAccount())
                .password(user.getPassword())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .status(user.getStatus())
                .registeredAt(user.getRegisteredAt())
                .unregisteredAt(user.getUnregisteredAt())
                .build();


        //Header + data return

        return userApiResponse;

    }


    public Header<List<UserApiResponse>> search(Pageable pageable) {

        Page<User> users = userRepository.findAll(pageable);

        List<UserApiResponse> userApiResponseList = users.stream()
                .map(user -> response(user))
                .collect(Collectors.toList())
                ;

        return Header.OK(userApiResponseList);
    }

    public Header<UserOrderInfoApiResponse> orderInfo(Long id) {

        //user
        User user = userRepository.getOne(id);
        UserApiResponse userApiResponse = response(user);


        //orderGroup
        List<OrderGroup> orderGroupList = user.getOrderGroupList();
        List<OrderGroupApiResponse> orderGroupApiResponses = orderGroupList.stream()
                .map(orderGroup -> {
                    OrderGroupApiResponse orderGroupApiResponse = orderGroupApiLogicService.response(orderGroup).getData();

                    List<ItemApiResponse> itemApiResponsesList = orderGroup.getOrderDetailList().stream()
                            .map(detail -> detail.getItem())
                            .map(item -> itemApiLogicService.response(item).getData())
                            .collect(Collectors.toList());

                    orderGroupApiResponse.setItemApiResponseList(itemApiResponsesList);

                    return orderGroupApiResponse;

                })
                .collect(Collectors.toList());

        userApiResponse.setOrderGroupApiResponseList(orderGroupApiResponses);

        UserOrderInfoApiResponse userOrderInfoApiResponse = UserOrderInfoApiResponse.builder()
                .userApiResponse(userApiResponse)
                .build();

        return Header.OK(userOrderInfoApiResponse);

    }
}
