package com.imbananko.config;

import com.google.inject.AbstractModule;
import com.imbananko.dao.AccountDAO;
import com.imbananko.dao.AccountDAOImpl;
import com.imbananko.dao.TransactionDAO;
import com.imbananko.dao.TransactionDAOImpl;
import com.imbananko.service.AccountService;
import com.imbananko.service.TransactionHistoryService;
import com.imbananko.service.TransferService;

public class BurlesqueModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(AccountDAO.class).to(AccountDAOImpl.class).asEagerSingleton();
        bind(TransactionDAO.class).to(TransactionDAOImpl.class).asEagerSingleton();
        requestStaticInjection(AccountService.class);
        requestStaticInjection(TransactionHistoryService.class);
        requestStaticInjection(TransferService.class);
    }
}
