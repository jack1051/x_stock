package project.common.proxy;

import project.stock.Mornit_stock;

import java.io.IOException;

/**
 * 测试类
 */
public class Test {



    public  static void main (String[] a) throws IOException {
//        //目标对象
//        UserDao target = new UserDao();
//
//        //代理对象
//        UserDao proxy = (UserDao)new ProxyThreadFactory(target).getProxyInstance();
//
//        //执行代理对象的方法
//        proxy.save("sh600887", "<",100.00);


        Mornit_stock mornit_stock = new Mornit_stock();
        //代理对象
        Mornit_stock m_proxy = (Mornit_stock)new ProxyThreadFactory(mornit_stock).getProxyInstance();
        m_proxy.celueByMonit_data("sh600887", "<",100.00 );

        m_proxy.celueForScode(  "sz000999", "<",  "d_m");

        m_proxy.celueForScode(  "sh601398", "<",  "d_m");
        m_proxy.celueForScode(  "sh601398", "=",  "d_l");
        m_proxy.celueForScode(  "sh601398", "<",  "d_l");

        m_proxy.celueForScode(  "sh600887", "<",  "d_l");

    }
}