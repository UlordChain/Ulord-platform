/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.iptuils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author haibo
 * @since 7/17/18
 */
public class SubnetUtils {
    private static final String IP_ADDRESS = "(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})";
    private static final String SLASH_FORMAT = IP_ADDRESS + "/(\\d{1,3})";
    private static final Pattern addressPattern = Pattern.compile(IP_ADDRESS);
    private static final Pattern cidrPattern = Pattern.compile(SLASH_FORMAT);
    private static final int NBITS = 32;

    private long netmask = 0;
    private long address = 0;
    private long network = 0;
    private long broadcast = 0;

    public SubnetUtils(String ipCidr){
        calculate(ipCidr);
    }

    private void calculate(String mask) {
        Matcher matcher = cidrPattern.matcher(mask);

        if (matcher.matches()) {
            address = matchAddress(matcher);

            /* Create a binary netmask from the number of bits specification /x */
            int cidrPart = rangeCheck(Integer.parseInt(matcher.group(5)), 0, NBITS);
            for (int j = 0; j < cidrPart; ++j) {
                netmask |= (1 << 31 - j);
            }

            /* Calculate base network address */
            network = (address & netmask);

            /* Calculate broadcast address */
            broadcast = network | ~(netmask);
        } else {
            throw new IllegalArgumentException("Could not parse [" + mask + "]");
        }
    }

    private long matchAddress(Matcher matcher) {
        long addr = 0;
        for (int i = 1; i <= 4; ++i) {
            long n = (rangeCheck(Integer.parseInt(matcher.group(i)), 0, 255));
            addr |= ((n & 0xff) << 8*(4-i));
        }
        return addr;
    }

    private int rangeCheck(int value, int begin, int end) {
        if (value >= begin && value <= end) { // (begin,end]
            return value;
        }

        throw new IllegalArgumentException("Value [" + value + "] not in range ["+begin+","+end+"]");
    }

    public long getStartAddress(){
        return network;
    }

    public long getEndAddress(){
        return broadcast;
    }

    public static void main(String[] argv){
        String ipaddr = "144.0.0.0/16";
        SubnetUtils subnetUtils = new SubnetUtils(ipaddr);
        System.out.println(subnetUtils.getStartAddress() + " - " + subnetUtils.getEndAddress());
    }
}
