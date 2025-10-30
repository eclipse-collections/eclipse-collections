class Solution {
    public int search(int[] nums, int target) {
        int i = 0;
        int j = nums.length - 1;

        while (i <= j){
            int mid_i = i + (j - i) / 2;
            int mid_val = nums[mid_i];
            if (target < mid_val){
                j = mid_i - 1;
            }
            else if (target > mid_val){
                i = mid_i + 1;
            }
            else {
                return mid_i;
            }
        }
        return -1;
    }
}
