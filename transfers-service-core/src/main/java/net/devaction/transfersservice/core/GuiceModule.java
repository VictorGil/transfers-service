package net.devaction.transfersservice.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;

import net.devaction.transfersservice.core.account.Account;
import net.devaction.transfersservice.core.accountsmanager.AccountsManager;
import net.devaction.transfersservice.core.accountsmanager.AccountsManagerImpl;
import net.devaction.transfersservice.core.transfersmanager.TransferChecker;
import net.devaction.transfersservice.core.transfersmanager.TransferCheckerImpl;
import net.devaction.transfersservice.core.transfersmanager.TransfersManager;
import net.devaction.transfersservice.core.transfersmanager.TransfersManagerImpl;

/**
 * @author Víctor Gil
 *
 * since February 2020
 */
public class GuiceModule extends AbstractModule {

    @Override
    protected void configure() {

        bind(TransfersManager.class).to(TransfersManagerImpl.class).in(Scopes.SINGLETON);

        bind(AccountsManager.class).to(AccountsManagerImpl.class).in(Scopes.SINGLETON);

        bind(TransferChecker.class).to(TransferCheckerImpl.class).in(Scopes.SINGLETON);

        bind(new TypeLiteral<Map<String, Account>>(){})
                .to(new TypeLiteral<ConcurrentHashMap<String, Account>>(){})
                .in(Scopes.SINGLETON);
    }
}
