package com.jiayi.auth.security;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.jiayi.auth.security.interfaces.BaseCustomerSecurity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Admin
 */
@Service
@Slf4j
public class CustomerSecurityFilters {

    @Autowired
    private ApplicationContext context;

    /**
     * 所有CustomerSecurity实现
     */
    private Set<BaseCustomerSecurity> baseCustomerSecuritySet;

    /**
     * 获得所有security实现beans
     *
     * @return
     */
    public Set<BaseCustomerSecurity> customerSecurityList() {
        if (baseCustomerSecuritySet == null) {
            Map<String, BaseCustomerSecurity> customerSecurityMap = context.getBeansOfType(BaseCustomerSecurity.class);
            if (customerSecurityMap != null) {
                baseCustomerSecuritySet = Sets.newHashSet(customerSecurityMap.values());
            }
        }
        Assert.notNull(baseCustomerSecuritySet, "请添加CustomerSecurity的实现类");
        return baseCustomerSecuritySet;
    }


    /**
     * 获得无需登录即可访问的路径
     *
     * @return
     */
    public String[] ignorePath() {
        List<String> paths = Lists.newArrayList();
        customerSecurityList().forEach(customerSecurity -> {
            paths.addAll(Arrays.asList(customerSecurity.ignorePath()));
        });
        return paths.toArray(new String[paths.size()]);
    }
}
