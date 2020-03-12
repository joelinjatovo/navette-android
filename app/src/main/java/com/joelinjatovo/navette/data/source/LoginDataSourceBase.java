package com.joelinjatovo.navette.data.source;

import com.joelinjatovo.navette.data.Result;
import com.joelinjatovo.navette.database.entity.User;

public interface LoginDataSourceBase {

    public Result<User> login(String username, String password);

    public void logout() ;
}
