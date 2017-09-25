package mecha.id.objectrecognizer;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by insan on 9/25/2017.
 */

class TresholdObjectCounter {
    private int[][] tresholdImgArray;
    private int totalCountedTresholdObjects,minPixelsInObject = 10;
    private List<TresholdObject> tresholdObjectsArray;
    private String LOG = "TOC";

    public TresholdObjectCounter(int[][] tresholdImgArrayParam) {
        tresholdImgArray = tresholdImgArrayParam;
    }

    public int getTotalTresholdObjects() {

        countTresholdObjects();

        Log.e(LOG,"getTotalTresholdObjects() : "+String.valueOf(totalCountedTresholdObjects));
        return totalCountedTresholdObjects;
    }

    private void countTresholdObjects() {


        int i = 0;
        int[] firstObjectPixel;
        TresholdObject tresholdObject;


        while(i<10){

            firstObjectPixel = findFirstObjectPixel();

            tresholdObject = new TresholdObject(tresholdImgArray, firstObjectPixel[1], firstObjectPixel[2]);


            if (tresholdObject.getTotalPixelsInObject() >= minPixelsInObject) {
                totalCountedTresholdObjects += 1;
            }


                //floodFillTheObject(tresholdObject.getAllCoordinates());

                //countTresholdObjects();

            i++;
        }

    }

    private void floodFillTheObject(List<int[]> allCoordinates) {

    }


    private int[] findFirstObjectPixel() {

        int[] firstObjectPixel = {0,0,0}; //found, x, y

        outerloop:
        for (int j = 0; j < tresholdImgArray.length; j++) {
            for (int i = 0; i < tresholdImgArray[0].length; i++) {
                if (tresholdImgArray[j][i] == 1) {
                    firstObjectPixel[0] = 1;
                    firstObjectPixel[1] = i;
                    firstObjectPixel[2] = j;

                    Log.i(LOG, "findFirstObjectPixel() : " + String.valueOf(firstObjectPixel[1]) + " " + String.valueOf(firstObjectPixel[2]));

                    break outerloop;
                }
            }
        }


        return firstObjectPixel;
    }

    private class TresholdObject {

        private String LOG = "TOC";
        private int startX,startY,totalPixelsInObject = 0;
        int[][] tresholdImgArray;
        private List<int[]> edgeCoordinates = new ArrayList<int[]>();
        private List<int[]> allCoordinates = new ArrayList<int[]>();

        private int[][] edgeCoordinatesArray,allCoordinatesArray;


        private String chainCode = "";

        public TresholdObject(int[][] tresholdImgArrayParam, int startXParam, int startYParam) {

            edgeCoordinatesArray = new int[tresholdImgArrayParam.length][tresholdImgArrayParam[0].length];



            tresholdImgArray = tresholdImgArrayParam;
            startX = startXParam;
            startY = startYParam;

            trackTheObject(startX, startY, 4);

            Log.i(LOG,"TresholdObject() "+String.valueOf(edgeCoordinatesArray[2][2])+" : start x = "+String.valueOf(startX)+" start y = "+String.valueOf(startY));
        }

        private void trackTheObject(int currentXParam, int currentYParam, int initNextDirectionParam) {

            int nextDirection,initNextDirectionAfter,initNextDirection,currentX,currentY,xToCheck,yToCheck,found = 0;
            boolean stop = false;

            currentX = currentXParam;
            currentY = currentYParam;
            initNextDirection = initNextDirectionParam;

            mainwhile:
            while(stop==false){

                edgeCoordinates.add(new int[]{currentXParam,currentYParam});
                allCoordinates.add(new int[]{currentXParam,currentYParam});
                edgeCoordinatesArray[currentX][currentY] = 1;
                Log.i(LOG,"Add edgeCoordinates. Total edgeCoordinates : "+String.valueOf(edgeCoordinates.size()));

                for(int i=initNextDirection;i<initNextDirection+8;i++){

                    if(i>9){
                        nextDirection = i - 8;
                    }else{
                        nextDirection = i;
                    }

                    switch(nextDirection){
                        case 2:
                            xToCheck = currentX;
                            yToCheck = currentY-1;
                            initNextDirectionAfter = 9;
                            break;
                        case 3:
                            xToCheck = currentX+1;
                            yToCheck = currentY-1;
                            initNextDirectionAfter = 9;
                            break;
                        case 4:
                            xToCheck = currentX+1;
                            yToCheck = currentY;
                            initNextDirectionAfter = 3;
                            break;
                        case 5:
                            xToCheck = currentX+1;
                            yToCheck = currentY+1;
                            initNextDirectionAfter = 3;
                            break;
                        case 6:
                            xToCheck = currentX;
                            yToCheck = currentY+1;
                            initNextDirectionAfter = 5;
                            break;
                        case 7:
                            xToCheck = currentX-1;
                            yToCheck = currentY+1;
                            initNextDirectionAfter = 5;
                            break;
                        case 8:
                            xToCheck = currentX-1;
                            yToCheck = currentY;
                            initNextDirectionAfter = 7;
                            break;
                        case 9:
                            xToCheck = currentX-1;
                            yToCheck = currentY-1;
                            initNextDirectionAfter = 7;
                            break;
                        default:
                            xToCheck = currentX;
                            yToCheck = currentY;
                            initNextDirectionAfter = 1;
                    }

                    if(tresholdImgArray[yToCheck][xToCheck] == 1){
                        chainCode += String.valueOf(nextDirection);
                        found=1;

                        Log.i("Track "+LOG,String.valueOf(xToCheck)+", "+String.valueOf(yToCheck)+" found : "
                                +String.valueOf(found)
                                +" chain code : "+chainCode);


                        if(xToCheck!=startX||yToCheck!=startY){
                            Log.i(LOG,"beda ama titik awal");
                            currentX = xToCheck;
                            currentY = yToCheck;

                            initNextDirection = initNextDirectionAfter;
                            continue mainwhile;
                        }

                        if(xToCheck==startX&&yToCheck==startY){
                            Log.i(LOG,"Terkelilingi. Kembali ke titik awal");
                            stop = true;
                        }

                        break;
                    }else{
                        Log.i("Track "+LOG,String.valueOf(xToCheck)+", "+String.valueOf(yToCheck)+" found : "+found);
                    }

                }

                if(edgeCoordinates.size()==1 && found==0){
                    Log.e(LOG,"LONELY PIXEL");
                }

            }


        }

        public List<int[]> getAllCoordinates() {
            return allCoordinates;
        }

        public int getTotalPixelsInObject() {

            int totalEdgesCoordinatesInRow;

            for(int y=0;y<edgeCoordinatesArray.length;y++){

                totalEdgesCoordinatesInRow = 0;

                for(int x=0;x<edgeCoordinatesArray[0].length;x++){
                    if(edgeCoordinatesArray[y][x]==1){
                        //totalEdgesCoordinatesInRow += 1;
                        //Log.i(LOG,"edgeCoordinatesArray["+String.valueOf(y)+"]["+String.valueOf(x)+"]==1");
                    }else{
                        //bandingin ke tresholdImgArray
                        if(edgeCoordinatesArray[y][x-1]==1){
                            if(tresholdImgArray[y][x]==1){
                                //horee
                                edgeCoordinatesArray[y][x]=1;
                            }
                        }

                    }
                }

                /*
                for(int x=0;x<edgeCoordinatesArray[0].length;x++){
                    if(edgeCoordinatesArray[y][x]==1){
                        totalEdgesCoordinatesInRow -= 1;
                    }
                    if(edgeCoordinatesArray[y][x]==0){
                        if(edgeCoordinatesArray[y][x-1]==1){
                            if(totalEdgesCoordinatesInRow > 0){
                                allCoordinates.add(new int[]{y,x});
                            }
                        }
                    }
                }
                */

                //bandingin ke tresholdImgArray



                Log.i(LOG,"totalEdgesCoordinatesInRow() - "+String.valueOf(totalEdgesCoordinatesInRow));


            }

            return totalPixelsInObject;
        }
    }
}
