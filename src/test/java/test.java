import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class test {
    public String reverseStr(String s, int k) {
        char[] carr = s.toCharArray();
        int i=0;
        int n=carr.length;
        while (i+2*k<n){
            reverse(carr,i,i+k-1);
            i = i+2*k;
        }
        if(carr.length-i<k){
            reverse(carr,i,carr.length-1);
        }else if(carr.length-i>=k){
            reverse(carr,i,i+k-1);
        }
        return new String(carr);
    }
    public void reverse(char[] c,int start,int end){
        char temp;
        int n = end-start+1;
        for(int i=start;i<start+n/2;i++){
            temp = c[i];
            c[i] = c[start+end-i];
            c[start+end-i] = temp;
        }
    }
    public void reverse(char[] chars){
        char temp;
        for(int i=0;i<chars.length/2;i++){
            temp = chars[i];
            chars[i] = chars[chars.length-1-i];
            chars[chars.length-1-i] = temp;
        }
    }
    public static int totalFruit(int[] tree) {
        // We'll make a list of indexes for which a block starts.
        List<Integer> blockLefts = new ArrayList<>();

        // Add the left boundary of each block
        for (int i = 0; i < tree.length; ++i)
            if (i == 0 || tree[i-1] != tree[i])
                blockLefts.add(i);

        // Add tree.length as a sentinel for convenience
        blockLefts.add(tree.length);

        int ans = 0, i = 0;
        search: while (true) {
            // We'll start our scan at block[i].
            // types : the different values of tree[i] seen
            // weight : the total number of trees represented
            //          by blocks under consideration
            Set<Integer> types = new HashSet();
            int weight = 0;

            // For each block from the i-th and going forward,
            for (int j = i; j < blockLefts.size() - 1; ++j) {
                // Add each block to consideration
                types.add(tree[blockLefts.get(j)]);
                weight += blockLefts.get(j+1) - blockLefts.get(j);

                // If we have 3+ types, this is an illegal subarray
                if (types.size() >= 3) {
                    i = j - 1;
                    continue search;
                }

                // If it is a legal subarray, record the answer
                ans = Math.max(ans, weight);
            }

            break;
        }

        return ans;
    }
    public static void main(String[] args) {
        int[] arr = {0,1,2,2};
        System.out.println(totalFruit(arr));
    }
    public List<List<Integer>> threeSum(int[] nums) {
        if (nums.length < 3){
            return new ArrayList<>();
        }
        Arrays.sort(nums);
        List<List<Integer>> intList = new ArrayList<>();
        for (int i=0;i<nums.length;i++){
            if (i>0&&(nums[i]==nums[i-1])){
                continue;
            }
            int third = nums.length-1;
            int target = -nums[i];
            for(int j=i+1;j<nums.length;j++){
                if (j>i+1&&(nums[j]==nums[j-1])){
                    continue;
                }
                while (j<third && nums[j]+nums[third]>target){
                    third--;
                }
                if (j==third){
                    break;
                }
                if (nums[j]+nums[third]==target){
                    List<Integer> list = new ArrayList<>();
                    list.add(nums[i]);
                    list.add(nums[j]);
                    list.add(nums[third]);
                    intList.add(list);
                }
            }
        }
        return intList;
    }
    public List<List<Integer>> fourSum(int[] nums, int target) {
        Arrays.sort(nums);
        List<List<Integer>> inList = new ArrayList<>();
        for (int first=0;first<nums.length;first++){
            if (first>0 && nums[first]==nums[first-1]){
                continue;
            }
            for (int second=first+1;second<nums.length;second++){
                if (second>first+1 && nums[second]==nums[second-1]){
                    continue;
                }
                for (int third=second+1;third<nums.length;third++){
                    if (third>second+1 && nums[third]==nums[third-1]){
                        continue;
                    }
                    int forth = nums.length-1;
                    int tar = target-nums[first]-nums[second];
                    while (forth>third && nums[third]+nums[forth]>tar){
                        forth--;
                    }
                    if (third == forth){
                        break;
                    }
                    if (nums[third]+nums[forth]==tar){
                        List<Integer> list = new ArrayList<>();
                        list.add(nums[first]);
                        list.add(nums[second]);
                        list.add(nums[third]);
                        list.add(nums[forth]);
                        inList.add(list);
                    }
                }
            }
        }
        return inList;
    }

    public int fib(int n) {
        if (n==0){
            return 0;
        }
        if (n==1){
            return 1;
        }
        return fib(n-1)+fib(n-2);
    }

    @Test
    public void test02(){
        System.out.println(2<<1);
    }

}
