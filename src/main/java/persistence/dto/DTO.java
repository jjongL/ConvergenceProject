package persistence.dto;

import network.Deserializer;
import network.Serializer;

import persistence.enums.Authority;
import persistence.enums.Enum;
import persistence.enums.OrdersStatus;
import persistence.enums.RegistStatus;

import sharing.Serializable;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;

public abstract class DTO implements Serializable {
    @Override
    public byte[] getBytes() {
        ArrayList<Byte> result = new ArrayList<>();
        Field[] classMembers = this.getClass().getDeclaredFields();
        boolean isDynamic;

        for(int i = 0; i < classMembers.length; i++) {
            classMembers[i].setAccessible(true);
            String type = classMembers[i].getType().toString();
            isDynamic = false;

            byte[] arr = new byte[0];
            try {
                Object memberVal = classMembers[i].get(this);

                if (type.equals("int")) {
                    arr = Serializer.intToByteArray((int)memberVal);
                }
                else if (type.equals("long")) {
                    arr = Serializer.longToByteArray((long)memberVal);
                }
                else if (type.contains("String")) {
                    arr = ((String)memberVal).getBytes();
                    isDynamic = true;
                }
                else if (type.contains("LocalDateTime")) {
                    arr = Serializer.dateToByteArray((LocalDateTime)memberVal);
                    isDynamic = true;
                }
                else if (type.contains("enums")) {
                    arr = Serializer.enumToByteArray((Enum)memberVal);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            if (isDynamic) {
                int length = arr.length;
                byte[] lengthByteArray = Serializer.intToByteArray(length);
                for(int j = 0; j < lengthByteArray.length; j++) {
                    result.add(lengthByteArray[j]);
                }
            }

            for(int j = 0; j < arr.length; j++) {
                result.add(arr[j]);
            }
        }

        byte[] returnArray = new byte[result.size()];
        for(int i = 0; i < result.size(); i++) {
            returnArray[i] = result.get(i);
        }

        return returnArray;
    }

    public void setMembersByByteArray(byte[] arr) {
        final int INT_LENGTH = 4;
        final int LONG_LENGTH = 8;
        int idx = 0;
        Field[] classMembers = this.getClass().getDeclaredFields();

        for(int i = 0; i < classMembers.length; i++) {
            classMembers[i].setAccessible(true);
            String type = classMembers[i].getType().toString();

            try {
                Object memberVal = classMembers[i].get(this);

                if (type.equals("int")) {
                    byte[] intByteArray = new byte[INT_LENGTH];
                    System.arraycopy(intByteArray, 0, arr, idx, INT_LENGTH); idx += INT_LENGTH;
                    memberVal = Deserializer.byteArrayToInt(intByteArray);
                }
                else if (type.equals("long")) {
                    byte[] longByteArray = new byte[LONG_LENGTH];
                    System.arraycopy(longByteArray, 0, arr, idx, LONG_LENGTH); idx += LONG_LENGTH;
                    memberVal = Deserializer.byteArrayToInt(longByteArray);
                }
                else if (type.contains("String")) {
                    byte[] lengthByteArray = new byte[INT_LENGTH];
                    System.arraycopy(lengthByteArray, 0, arr, idx, INT_LENGTH); idx += INT_LENGTH;
                    int length = Deserializer.byteArrayToInt(lengthByteArray);

                    byte[] stringByteArray = new byte[length];
                    System.arraycopy(stringByteArray, 0, arr, idx, length); idx += length;
                    memberVal = new String(stringByteArray);
                }
                else if (type.contains("LocalDateTime")) {
                    byte[] yearByteArray = new byte[INT_LENGTH];
                    byte[] monthByteArray = new byte[INT_LENGTH];
                    byte[] dayByteArray = new byte[INT_LENGTH];
                    byte[] hourByteArray = new byte[INT_LENGTH];
                    byte[] minuteByteArray = new byte[INT_LENGTH];

                    System.arraycopy(yearByteArray, 0, arr, idx, INT_LENGTH); idx += INT_LENGTH;
                    System.arraycopy(monthByteArray, 0, arr, idx, INT_LENGTH); idx += INT_LENGTH;
                    System.arraycopy(dayByteArray, 0, arr, idx, INT_LENGTH); idx += INT_LENGTH;
                    System.arraycopy(hourByteArray, 0, arr, idx, INT_LENGTH); idx += INT_LENGTH;
                    System.arraycopy(minuteByteArray, 0, arr, idx, INT_LENGTH); idx += INT_LENGTH;

                    int year = Deserializer.byteArrayToInt(yearByteArray);
                    int month = Deserializer.byteArrayToInt(monthByteArray);
                    int day = Deserializer.byteArrayToInt(dayByteArray);
                    int hour = Deserializer.byteArrayToInt(hourByteArray);
                    int minute = Deserializer.byteArrayToInt(minuteByteArray);

                    LocalDateTime result = LocalDateTime.of(year, month, day, hour, minute);
                    memberVal = result;
                }
                else if (type.contains("Authority")) {
                    byte[] codeByteArray = new byte[INT_LENGTH];
                    System.arraycopy(codeByteArray, 0, arr, idx, INT_LENGTH); idx += INT_LENGTH;
                    memberVal = Authority.of(Deserializer.byteArrayToInt(codeByteArray));
                }
                else if (type.contains("OrderStatus")) {
                    byte[] codeByteArray = new byte[INT_LENGTH];
                    System.arraycopy(codeByteArray, 0, arr, idx, INT_LENGTH); idx += INT_LENGTH;
                    memberVal = OrdersStatus.of(Deserializer.byteArrayToInt(codeByteArray));
                }
                else if (type.contains("RegistStatus")) {
                    byte[] codeByteArray = new byte[INT_LENGTH];
                    System.arraycopy(codeByteArray, 0, arr, idx, INT_LENGTH); idx += INT_LENGTH;
                    memberVal = RegistStatus.of(Deserializer.byteArrayToInt(codeByteArray));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
