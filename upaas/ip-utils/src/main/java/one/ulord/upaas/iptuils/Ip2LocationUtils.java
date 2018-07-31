/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.iptuils;


import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * @author haibo
 * @since 7/17/18
 */
public enum Ip2LocationUtils {
    INSTANCE;

    private static final String IP_ADDRESS = "(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})";
    private static final String SLASH_FORMAT = IP_ADDRESS + "/(\\d{1,3})";
    private static final Pattern addressPattern = Pattern.compile(IP_ADDRESS);
    private static final Pattern cidrPattern = Pattern.compile(SLASH_FORMAT);
    private static final int NBITS = 32;

//    String ip2LocationFile = "IP2LOCATION-LITE-DB1.CSV";
    String ip2LocationFile = "GeoLite2-Country-Blocks-IPv4.csv";
    String countryFile = "GeoLite2-Country-Locations-zh-CN.csv";
    private HashMap<Integer, String> id2Country = new HashMap<>();

    private ArrayList<IpLocationItem> ip2LocationList = new ArrayList<>();

    Ip2LocationUtils(){
        // Load Country
        loadCountry();

        File file = new File(this.getClass().getClassLoader().getResource(ip2LocationFile).getFile());
        try {
            BufferedReader bis = new BufferedReader(new FileReader(file));
            String line;
            boolean skipFirstLine = true;
            while ((line = bis.readLine()) != null) {
                if (skipFirstLine){
                    skipFirstLine = false;
                    continue;
                }
                // process the line.
                String items[] = line.split(",");
                SubnetUtils subnetUtils = new SubnetUtils(items[0]);
//                System.out.println(line);
                if (items[1].length() > 0) {
                    ip2LocationList.add(new IpLocationItem(subnetUtils.getStartAddress(), subnetUtils.getEndAddress(),
                            id2Country.get(Integer.parseInt(items[1])), ""));
                }else if (items[2].length() > 0){
                    ip2LocationList.add(new IpLocationItem(subnetUtils.getStartAddress(), subnetUtils.getEndAddress(),
                            id2Country.get(Integer.parseInt(items[2])), ""));
                }

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadCountry() {
        File file = new File(this.getClass().getClassLoader().getResource(countryFile).getFile());
        try {
            BufferedReader bis = new BufferedReader(new FileReader(file));
            String line;
            boolean skipFirstLine = true;
            while ((line = bis.readLine()) != null) {
                if (skipFirstLine){
                    skipFirstLine = false;
                    continue;
                }
                // process the line.
                String items[] = line.split(",");
                id2Country.put(Integer.parseInt(items[0]), items[4]);

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Return CN or -
     * @param ip
     * @return
     */
    public String getIpCN(String ip){
        long ipv = getIpNum(ip);
        // 二分法查找范围
        int idx = Collections.binarySearch(ip2LocationList, ipv);
        if (idx >= 0){
            return ip2LocationList.get(idx).cn;
        }else{
            return "-";
        }
    }



    static long getIpNum(String ipAddress) {
        String [] ip = ipAddress.split("\\.");
        long a = Integer.parseInt(ip[0]);
        long b = Integer.parseInt(ip[1]);
        long c = Integer.parseInt(ip[2]);
        long d = Integer.parseInt(ip[3]);

        long ipNum = a * 256 * 256 * 256 + b * 256 * 256 + c * 256 + d;
        return ipNum;
    }

    class IpLocationItem implements Comparable<Long>{
        private long start;
        private long end;
        private String cn;
        private String country;

        public IpLocationItem(long start, long end, String cn, String country){
            this.start = start;
            this.end = end;
            this.cn = cn;
            this.country = country;
        }

        IpLocationItem(String line){
            String items[] = line.split(",");
            if (items.length == 4){
                if (items[0].startsWith("\"") || items[0].startsWith("'")){
                    start = Integer.parseInt(items[0].substring(1, items[0].length() - 1));
                }else{
                    start = Integer.parseInt(items[0]);
                }
                if (items[1].startsWith("\"") || items[1].startsWith("'")){
                    end = Integer.parseInt(items[1].substring(1, items[1].length() - 1));
                }else{
                    end = Integer.parseInt(items[1]);
                }
                if (items[2].startsWith("\"") || items[2].startsWith("'")){
                    cn = (items[2].substring(1, items[2].length() - 1));
                }else{
                    cn = (items[2]);
                }
                if (items[3].startsWith("\"") || items[3].startsWith("'")){
                    country = (items[3].substring(1, items[3].length() - 1));
                }else{
                    country = (items[3]);
                }
            }
        }

        @Override
        public int compareTo(Long o) {
            if (this.start <= o && o <= this.end) {
                return 0;
            }else if (o < this.start){
                return 1;
            }else{
                return -1;
            }
        }
    }
}
