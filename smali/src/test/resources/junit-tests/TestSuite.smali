.class public LAllTests;
.super Ljava/lang/Object;

#This class is a wrapper for all the classes in the junit-tests directory
#
#To run the tests, you need to use dx on the junit jar and push that to the
#device/emulator, and then zip up the classes.dex containing this class
#and all of the tests and push that to the device/emulator.
#
#dx --dex --output=classes.dex junit-4.6.jar
#zip junit-4.6.zip classes.dex
#adb push junit-4.6.zip /data/local
#java -jar smali.jar --dex --output=classes.dex .
#zip smali-junit-tests.zip classes.dex
#adb push smali-junit-tests.zip /data/local
#adb shell dalvikvm -cp /data/local/junit-4.6.zip:/data/local/smali-junit-tests.zip org.junit.runner.JUnitCore AllTests

.annotation runtime Lorg/junit/runner/RunWith;
    value = Lorg/junit/runners/Suite;
.end annotation

.annotation runtime Lorg/junit/runners/Suite$SuiteClasses;
    value = {   LFieldTest;,
                LStaticFieldInitializerTest;,
                LAnnotationTests;,
                LArrayTests;,
                LFormat31t;,
                LExceptionTest;
            }
.end annotation