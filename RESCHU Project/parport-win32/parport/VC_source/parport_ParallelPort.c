#include <conio.h>
#include "parpor~1.h"

JNIEXPORT jint JNICALL Java_parport_ParallelPort_readOneByte
  (JNIEnv * algo, jclass otro, jint portStatus)
{
   unsigned short status;
   status = (unsigned short)portStatus;
   return _inp(status);
}

JNIEXPORT void JNICALL Java_parport_ParallelPort_writeOneByte
  (JNIEnv * algo, jclass otro, jint portData, jint oneByte)
{
   unsigned short data;
   int aByte;
   data = (unsigned short)portData;
   aByte = (int)oneByte;
   _outp(data,aByte);
}




