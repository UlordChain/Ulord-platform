package one.ulord.upaas.iptuils; /**
 * Copyright(c) 2018
 * Ulord core developers
 */

/**
 * @author haibo
 * @since 7/17/18
 */
public class IPUtilsApplication {
    public static void main(String[] args){
        String[] ipList = {"144.0.85.62", "36.23.85.174", "125.43.87.47", "115.159.101.72", "123.180.19.73", "117.28.96.60",
            "115.159.101.72", "122.236.149.208", "36.23.85.174", "110.85.13.86", "122.236.149.208", "42.232.227.119", "36.23.85.174",
                "112.38.155.76", "18.216.102.20"
        };
        for (String item : ipList){
            System.out.println(item + " - " + Ip2LocationUtils.INSTANCE.getIpCN(item));
        }
    }
}

