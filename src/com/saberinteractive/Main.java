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
                dataOutputStream.writeInt(map.get(temp.rand));
                dataOutputStream.writeUTF(temp.data);
                temp = temp.next;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deserialize(InputStream inputStream) {
        try {
            DataInputStream dataInputStream = new DataInputStream(inputStream);
            this.count = dataInputStream.readInt();
            System.out.println("count:" + this.count);
            ArrayList<Integer> arrayId = new ArrayList<>(Collections.nCopies(this.count, 0));
            int id, randomId;
            String data;
            for (int i = 0; i < this.count; i++) {
                id = dataInputStream.readInt();
                randomId = dataInputStream.readInt();
                arrayId.set(id, randomId);
                data = dataInputStream.readUTF();
                System.out.println(id + " " + randomId + " " + data);
                this.addToNode(data);
            }
            dataInputStream.close();
            inputStream.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        ArrayList<ListNode> list = new ArrayList<>();
        ListNode temp = this.head;
        for (int i = 0; i < this.count; i++) {
            list.add(temp);
            temp = temp.next;
        }
        temp = this.head;
        for (int i = 0; i < this.count; i++) {
            temp.rand = list.get(i);
            temp = temp.next;
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
        ListRand forSerialize = new ListRand();
        ListRand forDeserialize = new ListRand();
        ListNode temp = null;
        for (int i = 0; i < 10; i++) {
            temp = forSerialize.addToNode(temp);
            System.out.println(forSerialize.count + " : " + temp.data);
        }
        forSerialize.randomNode();
        temp = forSerialize.head;
        for (int i = 0; i < forSerialize.count; i++) {
            System.out.println(i + " : " + temp.data + " my rand : " + temp.rand.data);
            temp = temp.next;
        }
        forSerialize.head.data = "qqqqqqqq";
        try {
            forSerialize.serialize(new FileOutputStream("qwe"));
            forDeserialize.deserialize(new FileInputStream("qwe"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}