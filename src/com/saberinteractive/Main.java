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
        return (this.prev == elem.prev) && (this.next == elem.next) && (this.rand == elem.rand) && (this.data.equals(elem.data));
    }
}

class ListRand {
    public ListNode head;
    public ListNode tail;
    public int count;

    public void serialize(OutputStream outputStream) {
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        HashMap<ListNode, Integer> map = new HashMap<>();
        ListNode temp = this.head;

        for (int i = 0; i < this.count; i++) {
            map.put(temp, i);
            temp = temp.next;
        }

        try {
            temp = this.head;
            dataOutputStream.writeInt(this.count);

            for (int i = 0; i < this.count; i++) {
                dataOutputStream.writeInt(i);
                if (map.get(temp.rand) != null) {
                    dataOutputStream.writeInt(map.get(temp.rand));
                } else {
                    dataOutputStream.writeInt(-1);
                }
                dataOutputStream.writeUTF(temp.data);
                temp = temp.next;
            }

            dataOutputStream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deserialize(InputStream inputStream) {
        try {
            DataInputStream dataInputStream = new DataInputStream(inputStream);
            this.count = dataInputStream.readInt();
            ArrayList<Integer> arrayId = new ArrayList<>(Collections.nCopies(this.count, 0));
            int id, randomId;
            String data;

            for (int i = 0; i < this.count; i++) {
                id = dataInputStream.readInt();
                randomId = dataInputStream.readInt();
                arrayId.set(id, randomId);
                data = dataInputStream.readUTF();
                this.addToNode(data);
            }
            dataInputStream.close();
            inputStream.close();

            ArrayList<ListNode> list = new ArrayList<>();
            ListNode temp = this.head;

            for (int i = 0; i < this.count; i++) {
                list.add(temp);
                temp = temp.next;
            }

            temp = this.head;

            for (int i = 0; i < this.count; i++) {
                if (arrayId.get(i) != -1) {
                    temp.rand = list.get(arrayId.get(i));
                } else {
                    temp.rand = null;
                }
                temp = temp.next;
            }

        } catch (IOException ex) {
            ex.printStackTrace();
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

    public ListNode addToNodeRandomData() {
        ListNode temp = new ListNode();
        temp.next = null;
        temp.rand = null;
        temp.data = String.valueOf(10 + (int) (Math.random() * 1000));
        this.count++;
        if (this.head != null) {
            temp.prev = this.tail;
            this.tail.next = temp;
            this.tail = temp;
        } else {
            temp.prev = null;
            this.head = temp;
            this.tail = temp;
        }
        return temp;
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

    public void randomNode() {
        Random random = new Random();
        ListNode temp = this.head;
        for (int i = 0; i < this.count; i++) {
            temp.rand = this.getNode(random.nextInt(this.count - 1));
            temp = temp.next;
        }
    }

    public static void main(String[] args) {
        ListRand forSerialize = new ListRand();
        ListRand forDeserialize = new ListRand();
        ListNode temp;
        for (int i = 0; i < 10; i++) {
            temp = forSerialize.addToNodeRandomData();
            System.out.println(forSerialize.count + " : " + temp.data);
        }
        forSerialize.randomNode();
        forSerialize.head.data = "some data";
        forSerialize.head.rand = null;
        try {
            forSerialize.serialize(new FileOutputStream("filename"));
            forDeserialize.deserialize(new FileInputStream("filename"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ListNode temp1 = forDeserialize.head;
        temp = forSerialize.head;
        for (int i = 0; i < forSerialize.count; i++) {
            System.out.println("ser:" + temp.data + " deser:" + temp1.data);
            if ((temp.rand != null) && (temp1.rand != null)) {
                System.out.println(temp.rand.data + "   " + temp1.rand.data);
            }
            temp = temp.next;
            temp1 = temp1.next;
        }
    }
}