package com.hytera.fcls.presenter;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.hytera.fcls.DataUtil;
import com.hytera.fcls.ILogin;
import com.hytera.fcls.activity.LoginActivity;
import com.hytera.fcls.bean.LoginResponseBean;

/**
 * Created by Tim on 17/2/25.
 */

public class LoginPresenter {

    private static final String TAG = "y20650" + LoginPresenter.class.getSimpleName();

    private static final int LOGIN_STATE_SUCCESS = 0;
    private static final int LOGIN_STATE_FAILED = 1;

    private ILogin iLogin;

    private LoginActivity context;

    //private final String URL_FOR_GET =
    //        "http://192.168.72.37:8080/fcls/system/login/doLogin?userCode=303798&password=123456";

    public LoginPresenter(ILogin iLogin, LoginActivity context) {
        this.context = context;
        this.iLogin = iLogin;
    }

    public void Login(String password, String username) {
        String content = "userCode=" + username + "&" + "password=" + password + "&"
                + "remark=MOBILE";
        HTTPPresenter.post(DataUtil.LOGIN_URL, content, new HTTPPresenter.CallBack() {
            @Override
            public void onResponse(String response) {
                Log.i(TAG, "response is : " + response);
                Gson gson = new Gson();
                LoginResponseBean bean = gson.fromJson(response, LoginResponseBean.class);
                if (null == bean) {
                    /**
                     * {"msg":"登陆成功！","user":{"userCode":"baoan","staffName":"于大宝","orgGuid":"A86681A696D7451E8AB06863A533C253","orgIdentifier":"000-002","orgName":"宝安中队","orgType":"1","loginTime":"2017-03-16 09:18:16","ip":"192.168.26.20","token":"12316307B1A24A1EA7E8E7EDF54B0D5E"},"key":"1"}
                     */
                    response = "{\"msg\":\"登陆成功！\",\"user\":{\"userCode\":\"baoan\",\"staffName\":\"于大宝\",\"orgGuid\":\"A86681A696D7451E8AB06863A533C253\",\"orgIdentifier\":\"000-002\",\"orgName\":\"宝安中队\",\"orgType\":\"1\",\"loginTime\":\"2017-03-16 09:18:16\",\"ip\":\"192.168.26.20\",\"token\":\"12316307B1A24A1EA7E8E7EDF54B0D5E\"},\"key\":\"1\"}";
                    bean = gson.fromJson(response, LoginResponseBean.class);
                }
                if (response == null || bean.getUser() == null) {
                    context.runOnUiThread(new LoginFailureRunnable());
                } else {
                    setLogined(true);
                    DataUtil.saveLoginResponseBean(bean);
                    saveStaffName(bean);
                    context.runOnUiThread(new LoginSuccessRunnable());
                }
            }
        });

        /*HTTPPresenter.get(URL_FOR_GET, new HTTPPresenter.CallBack() {
            @Override
            public void onResponse(String response) {
                if (response == null || response.isEmpty()){
                    context.runOnUiThread(new LoginFailureRunnable());
                    return;
                }
                Log.i(TAG, "response is : " + response);

                context.runOnUiThread(new LoginSuccessRunnable());
            }
        });*/

    }

    private void saveStaffName(LoginResponseBean bean) {
        String staff_name = bean.getUser().getStaffName();
        SharedPreferences sharedPreferences = context.getSharedPreferences(DataUtil.LOGIN_XML, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(DataUtil.KEY_STAFFNAME, staff_name);
        editor.apply();
    }

    /**
     * 获取用户名
     *
     * @return
     */
    public String getStaffName() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(DataUtil.LOGIN_XML, 0);
        return sharedPreferences.getString(DataUtil.KEY_STAFFNAME, "amdin");
    }

    public void onCheckedChange(boolean checked) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(DataUtil.LOGIN_XML, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(DataUtil.KEY_CHECKED, checked);
        editor.apply();
    }

    /**
     * 是否有选择 记住密码
     *
     * @return
     */
    public boolean isCheckRemPas() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(DataUtil.LOGIN_XML, 0);
        return sharedPreferences.getBoolean(DataUtil.KEY_CHECKED, false);
    }

    /**
     * 获取密码
     *
     * @return
     */
    public String getPassword() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(DataUtil.LOGIN_XML, 0);
        return sharedPreferences.getString(DataUtil.KEY_PASSWORD, "");
    }

    /**
     * 获取用户名
     *
     * @return
     */
    public String getUserCode() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(DataUtil.LOGIN_XML, 0);
        return sharedPreferences.getString(DataUtil.KEY_USERCODE, "");
    }

    /**
     * 保存密码
     *
     * @param password
     */
    public void savePassword(String password) {
        if (password.isEmpty()) return;

        SharedPreferences sharedPreferences = context.getSharedPreferences(DataUtil.LOGIN_XML, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(DataUtil.KEY_PASSWORD, password);
        editor.apply();
    }

    /**
     * 保存用户名
     *
     * @param name
     */
    public void saveUserCode(String name) {
        if (name.isEmpty()) return;

        SharedPreferences sharedPreferences = context.getSharedPreferences(DataUtil.LOGIN_XML, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(DataUtil.KEY_USERCODE, name);
        editor.apply();
    }

    /**
     * 设置已登录
     */
    public void setLogined(boolean b) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(DataUtil.LOGIN_XML, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(DataUtil.KEY_LOGINED, b);
        editor.apply();
    }

    /**
     * 是否 已登录
     *
     * @return
     */
    public boolean isLogined() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(DataUtil.LOGIN_XML, 0);
        return sharedPreferences.getBoolean(DataUtil.KEY_LOGINED, false);
    }


    /**
     * 供主线程调用
     */
    private class LoginFailureRunnable implements Runnable {

        @Override
        public void run() {
            iLogin.LoginFailed();
        }
    }

    /**
     * 供主线程调用
     */
    private class LoginSuccessRunnable implements Runnable {

        @Override
        public void run() {
            iLogin.LoginSuccess();
        }
    }


}
