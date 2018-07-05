/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.ucwallet.client;

import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;
import io.ipfs.multihash.Multihash;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * UDFS Client
 * @author haibo
 * @since 7/5/18
 */
public class UDFSClient {
    private ArrayList<String> udfsGatewayList = new ArrayList<>();
    private IPFS udfs;

    /**
     * Create a UDFS client using a set of UDFS gateway address
     * @param udfsGatewayList UDFS gateway URLs
     */
    public UDFSClient(List<String> udfsGatewayList){
        udfsGatewayList.addAll(udfsGatewayList);
        init();
    }

    /**
     * Create a UDFS client using a gateway address
     * @param udfsGateway UDFS gateway URL
     */
    public UDFSClient(String udfsGateway){
        udfsGatewayList.add(udfsGateway);
        init();
    }

    private void init(){
        for(String gateway : udfsGatewayList){
            try{
                this.udfs = new IPFS(gateway);
            }catch (Exception e){
                // May be cannot connect gateway
            }
        }

        if (udfs == null){
            throw new RuntimeException(
                    "Cannot connect to UDFS gateway, please recheck parameters or contract to Ulord web site.");
        }
    }

    /**
     * Publish a resource data into UDFS
     * @param name a title for current resource name
     * @param data binary data for resource
     * @return UDFS hash
     */
    public String publishResource(String name, byte[] data){
        NamedStreamable.ByteArrayWrapper file = new NamedStreamable.ByteArrayWrapper(name, data);
        String errMessage;
        try {
            MerkleNode addResult = udfs.add(file).get(0);
            return addResult.hash.toString();
        } catch (IOException e) {
            errMessage = e.getMessage();
        }
        throw new RuntimeException("UDFS write error:" + errMessage);
    }

    /**
     * Publish a resource file into UDFS
     * @param filename a local file
     * @return UDFS hash
     */
    public String publishResource(String filename){
        NamedStreamable.FileWrapper file = new NamedStreamable.FileWrapper(new File(filename));
        String errMessage;
        try {
            MerkleNode addResult = udfs.add(file).get(0);
            return addResult.hash.toString();
        } catch (IOException e) {
            errMessage = e.getMessage();
        }
        throw new RuntimeException("UDFS write error:" + errMessage);
    }

    /**
     * Get hash content into a byte array
     * @param hash a UDFS hash
     * @return file content which stored in UDFS
     * @throws IOException
     */
    public byte[] getContent(String hash) throws IOException {
        Multihash filePointer = Multihash.fromBase58(hash);
        byte[] fileContents = udfs.cat(filePointer);

        return fileContents;
    }
}
