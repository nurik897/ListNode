package com.saberinteractive;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;


class ListNode {
    public ListNode prev;
    public ListNode next;
    public ListNode rand; // произвольный элемент внутри списка
    public String data;

    public boolean equals(ListNode elem) {
        if ((this.prev == elem.prev) && (this.next == elem.next) && (this.rand == elem.rand) && (this.data.equals(elem.data))) {
            return true;
        } else {
            return false;
        }
    }
}

class ListRand {
    public ListNode head;
    public ListNode tail;
    public int count;

    public void serialize(OutputStream outputStream) {
        HashMap<ListNode, Integer> map = new HashMap<>();
        ListNode temp = this.head;
        for (int i = 0; i < this.count; i++) {
            map.put(temp, i);
            temp = temp.next;
        }
        try {
            temp = this.head;
            ByteArrayOutputStream baos = new ByteArrayOutputStream(64);
            baos.write(this.count);
            for (int i = 0; i < this.count; i++) {
                baos.write(i);
                baos.write(map.get(temp.rand));
                baos.write(temp.data.length());
                baos.write(temp.data.getBytes());
                System.out.println("writing id:" + i + ", randomId:" + map.get(temp.rand) + ", sizeof data:" + temp.data.length() + ", data:" + temp.data);
                temp = temp.next;
            }
            baos.writeTo(outputStream);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deserialize(InputStream inputStream) {
        try {
            this.count = inputStream.read();
            ArrayList<Integer> arrayId = new ArrayList<>(Collections.nCopies(this.count,0));
            System.out.println("count:" + this.count);
            int a = -1, i = 0, id = 0, randomId, dataSize = 0;
            String data = "";
            while (true) {
                switch (i) {
                    case 0:
                        id = inputStream.read();
                        a = id;
                        break;
                    case 1:
                        randomId = inputStream.read();
                        a = randomId;
                        arrayId.set(id, randomId);
                        break;
                    case 2:
                        dataSize = inputStream.read();
                        a = dataSize;
                        break;
                    case 3:
                        byte[] b = new byte[dataSize];
                        if ((a = inputStream.read(b)) != dataSize) break;
                        data = new String(b);
                        this.addToNode(data);
                        System.out.println(data);
                        break;
                }
                i++;
                if (a == -1) break;
                else if (i == 4) i = 0;
            }
            System.out.println(arrayId);
            inputStream.close();
            ArrayList<ListNode> list = new ArrayList<>();
            ListNode temp = this.head;
            for (i = 0; i < this.count; i++) {
                list.add(temp);
                temp = temp.next;
            }
            temp = this.head;
            for (i = 0; i < this.count; i++) {
                temp.rand = list.get(i);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public ListNode getNode(int index) {
        if (index < this.count) {
            ListNode temp = this.head;
            for (int i = 0; i <= index; i++) {
                temp = temp.next;
            }
            return temp;
        } else return null;
    }

    public ListNode addToNode(ListNode prev) {
        if (prev != null) {
            ListNode temp = new ListNode();
            temp.prev = prev;
            temp.next = null;
            temp.data = String.valueOf(10 + (int) (Math.random() * 1000));
            temp.rand = null;
            prev.next = temp;
            this.tail = temp;
            this.count++;
            return temp;
        } else return this.addFirst();
    }

    public void addToNode(String data) {
        ListNode temp = new ListNode();
        temp.data = data;
        temp.next = null;
        temp.rand = null;
        if (this.head != null) {
            temp.prev = this.tail;
            this.tail.next = temp;
            this.tail = temp;
        } else {
            temp.prev = null;
            this.head = temp;
            this.tail = temp;
        }
    }

    public ListNode addFirst() {
        ListNode temp = new ListNode();
        temp.data = String.valueOf(10 + (int) (Math.random() * 1000));
        temp.prev = null;
        temp.next = null;
        temp.rand = null;
        this.head = temp;
        this.tail = temp;
        this.count++;
        return temp;
    }

    public void randomNode() {
        Random random = new Random();
        ListNode temp = this.head;
        for (int i = 0; i < this.count; i++) {
            temp.rand = this.getNode(random.nextInt(this.count - 1));
            temp = temp.next;
        }
    }

    public static void main(String[] args) {
        ListRand forSereal = new ListRand();
        ListRand forDeser = new ListRand();
        ListNode temp = null;
        for (int i = 0; i < 10; i++) {
            temp = forSereal.addToNode(temp);
            System.out.println(forSereal.count + " : " + temp.data);
        }
        forSereal.randomNode();
        temp = forSereal.head;
        for (int i = 0; i < forSereal.count; i++) {
            System.out.println(i + " : " + temp.data + " my rand : " + temp.rand.data);
            temp = temp.next;
        }
        forSereal.head.data = "qqqqqqqq";
        InputStream inputStream;
        OutputStream outputStream;
        try {
            outputStream = new FileOutputStream("qwe");
            inputStream = new FileInputStream("qwe");
            forSereal.serialize(outputStream);
            forDeser.deserialize(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}