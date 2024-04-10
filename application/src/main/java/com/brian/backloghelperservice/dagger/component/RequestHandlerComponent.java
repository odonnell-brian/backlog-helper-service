package com.brian.backloghelperservice.dagger.component;

import com.brian.backloghelperservice.dagger.module.DdbBacklogItemDaoModule;
import com.brian.backloghelperservice.dao.impl.DdbBacklogItemDaoImpl;
import dagger.Component;
import javax.inject.Singleton;

@Singleton
@Component(modules = DdbBacklogItemDaoModule.class)
public interface RequestHandlerComponent {

  DdbBacklogItemDaoImpl buildBacklogItemDao();
}
