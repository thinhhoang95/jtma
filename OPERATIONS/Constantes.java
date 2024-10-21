package OPERATIONS;

public class Constantes {
    //input files
    //route file(node, link, route) and airport file
    public static String NOM_AEROPORT = "PARIS_CDG";
    //public static String NOM_AEROPORT = "ATATURK_STD";
    //public static String NOM_AEROPORT = "ATATURK_PMS";
   //flight set prerequisite data
    //public static String NOM_FLIGHTSET = "28July2018"; 
    //public static String NOM_FLIGHTSET = "28July2018_100"; 
    public static String NOM_FLIGHTSET = "20170711_26L_ARRIVEES";
    //output results
    public static String NOM_RESULT = NOM_AEROPORT+NOM_FLIGHTSET;
    
    /***************************decision variables***********************************/    

    public static boolean SLOT_RTA_CHANGE=true;
    public static boolean SPEED_CHANGE=true;
    public static boolean ROUTE_CHANGE=true;

    public static boolean REGULAR_SEPARATION_IN_FINAL=false;
    public static boolean SW_SEPARATION_IN_FINAL=true;
   
  
    
    public static int DELTA_SPEED_PLUS=0; //en pourcent de la vitesse nominale
    public static int DELTA_SPEED_MOINS=20;

    public static int DELTA_T_RTA_PLUS=120;//in TIME SLOTS
    public static int DELTA_T_RTA_MOINS=60;
  
    public static double TIME_SLOT=5.0;// in seconds
    /**********************************************************************************/    
    

    
    /***************************sliding window parameters***********************************/

    public static final double T_INIT=0*3600;//3600*2;
    public static final double START_OF_DAY = T_INIT;
    
    public static final double T_FINAL=3600*28;//3600*28;
    public static final double END_OF_DAY = T_FINAL;  
    
    public static final double TIME_WINDOW_DURATION=3600*2;

    public static final double TIME_SHIFT=1200;

    /***************************************************************************************/


    public static double WEIGHT_AIRSPACE=1.0;
    public static double WEIGHT_DELAY=0.1;
    
    
    /***************************constante de conversion***********************************/ 
    public static double NM_TO_METER = 1852.0;
    public static double KT_TO_MS = 1852.0 / 3600.0;
    public static double MS_TO_KT = 3600.0/ 1852.0;
    public static double FEET_TO_METER = 0.3048006096012;
    public static double METER_TO_FEET = 1.0/0.3048006096012;

    public static double FL_TO_METER = 30.48006096012;
    public static double FTM_TO_MS = 0.3048006096012/ 60.0;
    public static double MS_TO_FTM = 60.0/0.3048006096012;

    public static double DEGRE_TO_RADIAN = 3.14159265359/180.0;
    public static double RADIAN_TO_DEGRE = 180.0/3.14159265359;

    public static double NORM_H = 3;//norme horizontale en nautique
    public static double NORM_V = 10;//norme verticale en FL

    public static double METER_TO_NORMH = 1/(NORM_H*NM_TO_METER);
    public static double NORMH_TO_METER = NORM_H*NM_TO_METER;

    public static double METER_TO_NORMV = 1/(NORM_V*FL_TO_METER);
    public static double NORMV_TO_METER = NORM_V*FL_TO_METER;

    public static double MPS_TO_NORMHPM = 60/(NORM_H*NM_TO_METER);
    public static double MPS_TO_NORMVPM = 60/(NORM_V*FL_TO_METER);
    public static double FL_TO_NORM = 1/(NORM_V*FL_TO_METER);
    public static double FTM_TO_NORMPM = 1/(NORM_V);

    /**********************************************************************************/


    //TABLE_SEPARATION[2][0]=6, precedent-catégorie 2 (heavy), suivant-catégorie-0(small)
    //public static double[][] TABLE_SEPARATION=
    //{{3*NM_TO_METER, 3*NM_TO_METER, 3*NM_TO_METER},
    //{4*NM_TO_METER, 3*NM_TO_METER, 3*NM_TO_METER},
    //{6*NM_TO_METER, 5*NM_TO_METER, 4*NM_TO_METER}};
    
 

    //TABLE_SEPARATION[2][0]=6, precedent-catégorie 2 (heavy), suivant-catégorie-0(small)
    public static double[][] TABLE_SEPARATION=
    {{3*NM_TO_METER, 3*NM_TO_METER, 3*NM_TO_METER,3*NM_TO_METER},
     {4*NM_TO_METER, 3*NM_TO_METER, 3*NM_TO_METER,3*NM_TO_METER},
     {6*NM_TO_METER, 5*NM_TO_METER, 4*NM_TO_METER,3*NM_TO_METER},
     {8*NM_TO_METER, 6*NM_TO_METER, 5*NM_TO_METER, 4*NM_TO_METER}};
    
 
    //TABLE_SEPARATION in time (sec)
       public static double[][] TABLE_DISTANCE_SEPARATION=
    {{3*NM_TO_METER, 3*NM_TO_METER, 3*NM_TO_METER,3*NM_TO_METER},
     {5*NM_TO_METER, 3*NM_TO_METER, 3*NM_TO_METER,3*NM_TO_METER},
     {6*NM_TO_METER, 5*NM_TO_METER, 4*NM_TO_METER,3*NM_TO_METER},
     {8*NM_TO_METER, 7*NM_TO_METER, 6*NM_TO_METER,3*NM_TO_METER}};
    
    //TABLE_SEPARATION for  FINAL (in NM)
    public static double[][] FINAL_DISTANCE_TABLE_SEPARATION=
    {{3*NM_TO_METER, 4*NM_TO_METER, 5*NM_TO_METER,5*NM_TO_METER ,6*NM_TO_METER},
     {3*NM_TO_METER, 3*NM_TO_METER, 4*NM_TO_METER,4*NM_TO_METER ,5*NM_TO_METER},
     {3*NM_TO_METER, 3*NM_TO_METER, 3*NM_TO_METER,3*NM_TO_METER ,4*NM_TO_METER},
     {3*NM_TO_METER, 3*NM_TO_METER, 2.5*NM_TO_METER,2.5*NM_TO_METER ,2.5*NM_TO_METER},
     {3*NM_TO_METER, 3*NM_TO_METER, 2.5*NM_TO_METER,2.5*NM_TO_METER ,2.5*NM_TO_METER}};
 
    //TABLE_SEPARATION for  FINAL in TIME (in sec)
    public static double[][] FINAL_TIME_TABLE_SEPARATION_SW=
    {{57.4, 70.6, 69.2, 85.7 , 100},
     {57.4, 70.6, 69.2, 85.7 , 100},
     {57.4, 70.6, 69.2, 85.7 , 100},
     {57.4, 70.6, 69.2, 85.7 , 100},
     {57.4, 70.6, 69.2, 85.7 , 100}};

    //TABLE_SEPARATION for  FINAL in TIME (in sec)
    public static double[][] FINAL_TIME_TABLE_SEPARATION_LW=
    {{57.4, 94.1, 120,   120,   120},
     {57.4, 70.6, 110.8, 120,   120},
     {57.4, 70.6, 83.1,  100,   100},
     {57.4, 70.6, 69.2,   85.7, 100},
     {57.4, 70.6, 69.2,   80,   80}};
     
     


    
    //Time separation for landing in seconds, using the norms in the work of Frankovich
    public static int[][] LANDING_SEPARATION = 
    {{82,69,60},
     {123,69,60},
     {207,157,96}};
  
    public static double[] LANDING_RWR_OCC_TIME={60.0,60.0,60.0};

    public static double beta=0.9;
}
