package ec.edu.utn.example.proyecto_calculadoravlsm;

import java.util.List;

/**
 * Validator class for VLSM Calculator
 * Contains methods to validate IP addresses, CIDR notation, and host requirements
 */
public class UIValidator {

    /**
     * Validates an IPv4 address
     * @param ip The IP address to validate
     * @return true if the IP is valid, false otherwise
     */
    public static boolean validateIPv4(String ip) {
        if (ip == null || ip.isEmpty()) {
            return false;
        }
        String regex = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
        return ip.matches(regex);
    }

    /**
     * Validates a CIDR prefix value
     * @param cidr The CIDR prefix to validate
     * @return true if the CIDR prefix is valid, false otherwise
     */
    public static boolean validateCidr(String cidr) {
        try {
            int cidrValue = Integer.parseInt(cidr);
            return cidrValue >= 1 && cidrValue <= 32;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Validates the subnet count
     * @param subnetsText The number of subnets as text
     * @return true if valid, false otherwise
     */
    public static boolean validateSubnetCount(String subnetsText) {
        try {
            int numSubnets = Integer.parseInt(subnetsText);
            return numSubnets > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Gets the valid subnet count or returns a default value
     * @param subnetsText The number of subnets as text
     * @param defaultValue The default value to return if invalid
     * @return The parsed subnet count or the default value
     */
    public static int getValidSubnetCount(String subnetsText, int defaultValue) {
        try {
            int numSubnets = Integer.parseInt(subnetsText);
            if (numSubnets < 0) return 0;  // Allow 0 subnets, only correct negative values
            if (numSubnets > 50) return 50; // Limit to prevent UI issues
            return numSubnets;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Validates a host count
     * @param hostText The host count as text
     * @return true if valid, false otherwise
     */
    public static boolean validateHostCount(String hostText) {
        try {
            int hosts = Integer.parseInt(hostText);
            return hosts > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Validates all host counts
     * @param hostTexts List of host counts as texts
     * @return true if all are valid, false otherwise
     */
    public static boolean validateAllHostCounts(List<String> hostTexts) {
        for (String hostText : hostTexts) {
            if (!validateHostCount(hostText)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Validates if the network can accommodate the requested hosts
     * @param cidr The CIDR prefix
     * @param totalHosts The total hosts requested
     * @return true if the network can accommodate the hosts, false otherwise
     */
    public static boolean canNetworkAccommodateHosts(int cidr, int totalHosts) {
        int availableAddresses = (int) Math.pow(2, 32 - cidr) - 2; // -2 for network and broadcast addresses
        return availableAddresses >= totalHosts;
    }
}