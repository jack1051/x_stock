package project.stock;


import org.apache.log4j.Logger;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static project.common.util.CallingHTTPUtil.httpURLConectionGET_STR;

/**
 * Created by XN_GOUCHAO on 2017/7/10.
 */
public class Mornit_stock {
    public  static final Logger LOGGER= Logger.getLogger(Mornit_stock.class);


    // url数据
    public static  String[] stock_data(String socde ) throws IOException {
        String url = "http://hq.sinajs.cn/list="+socde;   //
        Map paramMap = new HashMap();
        paramMap.put("url", url);
        String resultstr = httpURLConectionGET_STR(paramMap);
        String[] arr = resultstr.split(",");
        LOGGER.debug("参数：resultstr="+resultstr);
        return arr;

    }

    // 阈值数据
    public static Map monit_data(String socde ) throws IOException {


        Map<String,Map> bollmap  = new HashMap();
        //三九
        bollmap.put("sz000999",monit_data("sz000999",26.830,25.610,24.390,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00));
        //工行
        bollmap.put("sh601398",monit_data("sh601398",5.190,5.040,4.880,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00));
//        伊利
        bollmap.put("sh600887",monit_data("sh600887",53.000,46.720,40.430,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00));



        return bollmap.get(socde);


    }

    // 阈值数据
    public static Map monit_data(String socde,Double d_h,Double d_m ,Double d_l, Double m_h, Double m_m ,Double m_l ,Double w_h, Double w_m, Double w_l, Double y_h ,Double  y_m , Double y_l  ) throws IOException {

        Map f  = new HashMap();
        f.put("scode",socde);

        f.put("d_h",d_h);
        f.put("d_m",d_m);
        f.put("d_l",d_l);

        f.put("m_h",m_h);
        f.put("m_m",m_m);
        f.put("m_l",m_l);

        f.put("w_h",w_h);
        f.put("w_m",w_m);
        f.put("w_l",w_l);

        f.put("y_h",y_h);
        f.put("y_m",y_m);
        f.put("y_l",y_l);

       return f;
    }



    // 5.爱数网盘操作模块
    public static void main(String[] args) throws IOException {
        Mornit_stock mornit_stock = new Mornit_stock();
//        celue("sz000999","<",26.83 );
//        compare(  1.00,  ">",  0.11 );
//        compare_exct(  5.040,"=",5.040 );

        mornit_stock.celueForScode(  "sz000999", "<",  "d_m");

        mornit_stock.celueForScode(  "sh601398", "<",  "d_m");

        mornit_stock.celueForScode(  "sh600887", "<",  "d_l");

    }


    public void celueForScode(String scode,String syb, String key) throws IOException {
        Map f = monit_data(scode);
        Thread thread1 = new Thread () {
            @Override
            public void run() {
                try {
                    int i=0;
                    while ( i<1000000000){
                        celueByMonit_data(  scode,  syb,   key,  f);
                        i++;
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                } catch (IOException e) {

                }
            }
        };
        thread1.start();

    }

    public boolean celueByMonit_data(String scode,String syb, String key,Map f) throws IOException {
        String[] arr = stock_data(scode);
        String name = arr[0];
        String kaiPan = arr[1];
        Double dangqian = Double.valueOf( arr[3]);

        Double f_price = (Double)f.get(key);
        return compare_exct( dangqian, syb, f_price );
    }

    public  boolean celueByMonit_data(String scode,String syb, Double f_price )  {
        String[] arr = new String[0];
        try {
            arr = stock_data(scode);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String name = arr[0];
        String kaiPan = arr[1];
        Double dangqian = Double.valueOf( arr[3]);

        return compare_exct( dangqian, syb, f_price );
    }

    public boolean compare_exct(Double dangqian,String syb,Double f_price ){

        if (compare(  dangqian,  syb,  f_price )){
            LOGGER.debug( "当前价"+ dangqian+ " "+syb+" "+f_price);
            return true;
        }
        return false;
    }

    public boolean compare(Double dangqian,String syb,Double f_price ){

        if (syb.equals("<") && dangqian < f_price){
            return true;
        }
        if (syb.equals(">") && dangqian > f_price){
            return true;
        }

        if (syb.equals("=") && dangqian.toString().equals(f_price.toString()) ){
            return true;
        }
        return false;
    }

}
