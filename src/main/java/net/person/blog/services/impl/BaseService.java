package net.person.blog.services.impl;

import net.person.blog.utils.Constants;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class BaseService {

    private Sort.Direction SORT = Sort.Direction.DESC;

    Pageable checkPage(int page,int size){
        page = Math.max(page, Constants.Page.DEFAULT_PAGE);
        size = Math.max(size, Constants.Page.DEFAULT_SIZE);
        Sort sort = new Sort(Sort.Direction.DESC,"createTime","order");
        return PageRequest.of(page - 1, size,sort);
    }
}
