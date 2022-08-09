package com.imdb.imdbtitles;

import java.util.*;

public class Solution {

    public static void main(String[] args) {
        int[] A = new int[]{5, 6, 7, 1, 2};

        int result = new Solution().solution(A);
        System.out.println("result = " + result);

    }

    public int solution(int[] A) {
        int smallestInt = 1;

        if (A.length == 0) return smallestInt;

        Arrays.sort(A);

        if (A[0] > 1) return smallestInt;
        if (A[A.length - 1] <= 0) return smallestInt;

        for (int i = 0; i < A.length; i++) {
            if (A[i] == smallestInt) {
                smallestInt++;
            }
        }

        return smallestInt;
    }

}
