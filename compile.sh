javac OPERATIONS/FLIGHT/*.java
javac OPERATIONS/AIRSPACE/*.java
javac OPERATIONS/*.java
javac RECUIT/*.java


#java OPERATIONS.FLIGHT.FlightSet

#time java RECUIT.Recuit  ZGSZ_PMS ZGSZ_PMS
#time java RECUIT.Recuit  ZGSZ_PMS_80%_reduced ZGSZ_STD
#time java RECUIT.Recuit  ZGSZ_PMS_eliminate0 ZGSZ_PMS
#time java RECUIT.Recuit  ZGSZ_PMS_eliminate0_120%_reduced ZGSZ_PMS
#time java RECUIT.Recuit  ZGSZ_PMS_eliminate0_150%_reduced ZGSZ_PMS
time java RECUIT.Recuit  PMS_eliminate0_150%_reduced_filteredSAREX ZGSZ_PMS
#time java RECUIT.Recuit  ZGSZ_PMS_eliminate0_150%_reduced_SameSpeed ZGSZ_PMS
#time java RECUIT.Recuit  ZGSZ_PMS_py_eliminate0_outx2 ZGSZ_PMS
#time java RECUIT.Recuit  ZGSZ_2flights_SAREX ZGSZ_PMS


#time java RECUIT.Recuit ZGSZ_scaled_1.5 ZGSZ_PMS
#time java RECUIT.Recuit  ZGSZ_PMS_150percent_reduced_WrongSpeedUnit ZGSZ_PMS

#time java RECUIT.Recuit  ZGSZ_STD ZGSZ_STD
#time java RECUIT.Recuit  ZGSZ_STD_80%_reduced ZGSZ_STD
#time java RECUIT.Recuit  ZGSZ_STD_eliminate0 ZGSZ_STD
#time java RECUIT.Recuit  ZGSZ_STD_eliminate0_120%_reduced ZGSZ_STD
#time java RECUIT.Recuit ZGSZ_STD_eliminate0_150%_reduced ZGSZ_STD
# time java RECUIT.Recuit  STD_eliminate0_150%_reduced_filteredSAREX ZGSZ_STD
#time java RECUIT.Recuit  ZGSZ_STD_eliminate0_150%_reduced_SameSpeed ZGSZ_STD

#time java RECUIT.Recuit  Reduced_ZGSZ_STD_scaled_1.5 ZGSZ_STD

