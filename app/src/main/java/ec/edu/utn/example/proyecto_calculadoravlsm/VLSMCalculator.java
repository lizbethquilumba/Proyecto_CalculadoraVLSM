package ec.edu.utn.example.proyecto_calculadoravlsm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VLSMCalculator {

    private String baseIp;
    private int parentCidr;  // CIDR de la red base
    private List<Integer> hostRequirements;
    private List<SubnetResult> subnets;

    public VLSMCalculator(String baseIp, List<Integer> hostRequirements, int cidr) {
        this.baseIp = baseIp;
        this.parentCidr = cidr;
        // Ordenar los requisitos de host en orden descendente
        this.hostRequirements = new ArrayList<>(hostRequirements);
        Collections.sort(this.hostRequirements, Collections.reverseOrder());
        this.subnets = new ArrayList<>();
    }

    public List<SubnetResult> calculate() throws Exception {
        long currentIp = ipToInt(baseIp);
        long totalAddresses = (long) Math.pow(2, 32 - parentCidr);
        long networkEnd = ipToInt(baseIp) + totalAddresses - 1;

        for (int i = 0; i < hostRequirements.size(); i++) {
            int hosts = hostRequirements.get(i);

            // Calculate the required subnet CIDR
            int subnetCidr = getMinimumCidrForHosts(hosts, parentCidr);

            // Ensure the CIDR does not exceed the base CIDR
            if (subnetCidr < parentCidr) {
                throw new Exception("Subred " + (i + 1) + " requires a CIDR /" + subnetCidr + ", which is greater than the base /" + parentCidr);
            }

            // Calculate the number of available hosts
            int hostsAvailable = (subnetCidr == 31) ? 2 : (subnetCidr == 32) ? 1 : (int) Math.pow(2, 32 - subnetCidr) - 2;

            long requiredBlockSize = (long) Math.pow(2, 32 - subnetCidr);

            // Ensure correct subnet alignment
            long subnetAlignment = currentIp % requiredBlockSize;
            if (subnetAlignment != 0) {
                currentIp += (requiredBlockSize - subnetAlignment);
            }

            // Ensure there is enough space in the network
            if (currentIp + requiredBlockSize - 1 > networkEnd) {
                throw new Exception("No enough addresses in the base network to allocate Subred " + (i + 1) + ".");
            }

            // Create the subnet and add the result
            Subnet subnet = new Subnet(intToIp(currentIp), subnetCidr, hosts);
            subnets.add(new SubnetResult(
                    i + 1,
                    subnet.getIpBase(),
                    subnet.getCidr(),
                    subnet.getHostsRequested(),
                    subnet.getHostsAvailable(),
                    subnet.getBroadcast(),
                    subnet.getRangeIps()
            ));

            // Move to the next available IP
            currentIp += requiredBlockSize;
        }

        return subnets;
    }

    // Función para convertir IP en entero
    // Change this method to static
    public static long ipToInt(String ipAddress) throws Exception {
        String[] octets = ipAddress.split("\\.");
        if (octets.length != 4) {
            throw new Exception("Invalid IP address");
        }
        long ipInt = 0;
        for (int i = 0; i < 4; i++) {
            ipInt |= (Long.parseLong(octets[i]) << (24 - (i * 8)));
        }
        return ipInt;
    }

    // Función para convertir de entero a IP
    public static String intToIp(long ipInt) {
        return String.format("%d.%d.%d.%d", (ipInt >> 24) & 0xFF, (ipInt >> 16) & 0xFF, (ipInt >> 8) & 0xFF, ipInt & 0xFF);
    }

    // Método que calcula el CIDR mínimo para los hosts dados
    private int getMinimumCidrForHosts(int hosts, int parentCidr) {
        for (int cidr = 32; cidr >= parentCidr; cidr--) {
            if (Math.pow(2, 32 - cidr) - 2 >= hosts) {
                return cidr;
            }
        }
        return parentCidr;
    }

    // Clase que almacena los resultados de cada subred
    public static class SubnetResult {
        private int subredId; // Keep it private
        private String ipBase;
        private int cidr;
        private int hostsRequested;
        private int hostsAvailable;
        private String broadcast;
        private String rangeIps;

        public SubnetResult(int subredId, String ipBase, int cidr, int hostsRequested, int hostsAvailable, String broadcast, String rangeIps) {
            this.subredId = subredId;
            this.ipBase = ipBase;
            this.cidr = cidr;
            this.hostsRequested = hostsRequested;
            this.hostsAvailable = hostsAvailable;
            this.broadcast = broadcast;
            this.rangeIps = rangeIps;
        }

        // Getter methods to access private fields
        public int getSubredId() {
            return subredId;
        }

        public String getIpBase() {
            return ipBase;
        }

        public int getCidr() {
            return cidr;
        }

        public int getHostsRequested() {
            return hostsRequested;
        }

        public int getHostsAvailable() {
            return hostsAvailable;
        }

        public String getBroadcast() {
            return broadcast;
        }

        public String getRangeIps() {
            return rangeIps;
        }
    }

    // Clase auxiliar para Subnet (equivalente a la clase 'Subnet' en Python)
    public static class Subnet {
        private String ipBase;
        private int cidr;
        private int hostsRequested;
        private int hostsAvailable;
        private String broadcast;
        private String rangeIps;

        public Subnet(String ipBase, int cidr, int hostsRequested) {
            this.ipBase = ipBase;
            this.cidr = cidr;
            this.hostsRequested = hostsRequested;
            // Calculate other values needed for the subnet (e.g., broadcast and range of IPs)
            this.hostsAvailable = (cidr == 31) ? 2 : (cidr == 32) ? 1 : (int) Math.pow(2, 32 - cidr) - 2; // Example calculation
            this.broadcast = calculateBroadcast();  // Example: Implement the broadcast calculation
            this.rangeIps = calculateRangeIps();  // Example: Implement the range calculation
        }

        public String getIpBase() {
            return ipBase;
        }

        public int getCidr() {
            return cidr;
        }

        public int getHostsRequested() {
            return hostsRequested;
        }

        public int getHostsAvailable() {
            return hostsAvailable;
        }

        public String getBroadcast() {
            return broadcast;
        }

        public String getRangeIps() {
            return rangeIps;
        }

        // Example method for calculating the broadcast address
        private String calculateBroadcast() {
            try {
                // Special case for CIDR /31
                if (cidr == 31) {
                    return "N/A";  // No broadcast address in CIDR /31
                }
                // Special case for CIDR /32
                else if (cidr == 32) {
                    return "N/A";  // No broadcast address in CIDR /32
                }

                // Calculate broadcast address
                long baseInt = ipToInt(ipBase);
                long totalIps = (long) Math.pow(2, 32 - cidr);
                return intToIp(baseInt + totalIps - 1);
            } catch (Exception e) {
                return "Error calculating broadcast";
            }
        }

        // Example method for calculating the IP range
        private String calculateRangeIps() {
            try {
                long baseInt = ipToInt(ipBase);

                // Special case for CIDR /31 (RFC 3021)
                if (cidr == 31) {
                    // In CIDR /31, both addresses are usable for hosts
                    String ipStart = ipBase;
                    String ipEnd = intToIp(baseInt + 1);
                    return ipStart + " - " + ipEnd;
                }
                // Special case for CIDR /32
                else if (cidr == 32) {
                    // In CIDR /32, only one address is available
                    return ipBase;
                }
                // Normal case
                else {
                    String ipStart = intToIp(baseInt + 1);  // First usable IP (after network address)
                    String ipEnd = intToIp(baseInt + (long) Math.pow(2, 32 - cidr) - 2);  // Last usable IP (before broadcast)
                    return ipStart + " - " + ipEnd;
                }
            } catch (Exception e) {
                return "Error calculating range";
            }
        }
    }

    public static void main(String[] args) {
        try {
            List<Integer> hostRequirements = List.of(10, 20, 30);  // Ejemplo de requisitos de host
            VLSMCalculator calculator = new VLSMCalculator("192.168.1.0", hostRequirements, 24);
            List<SubnetResult> results = calculator.calculate();

            // Imprimir resultados
            for (SubnetResult result : results) {
                System.out.println("Subred " + result.subredId + ": " + result.ipBase + "/" + result.cidr);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}