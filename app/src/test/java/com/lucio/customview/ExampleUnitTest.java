package com.lucio.customview;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {

//        assertEquals(4, 2 + 2);

        List<String> list=new ArrayList<>();
        list.add("123");
        list.add("456");
        list.add("789");

        final String str="222";

        Observable.fromIterable(list).filter(new Predicate<String>() {
            @Override
            public boolean test(String s) {
                return s.equals("12555");
            }
        }).filter(new Predicate<String>() {
            @Override
            public boolean test(String s) {
                return str.contains("1");
            }
        }).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                System.out.println("onSubscribe");
                System.out.println(d.toString());
            }

            @Override
            public void onNext(String s) {
                System.out.println("onNext");
                System.out.println(s);
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("onError");
                System.out.println(e.getMessage());
            }

            @Override
            public void onComplete() {
                System.out.println("onComplete");
            }
        });

//        Observable.fromIterable(list).map(new Function<String, String>() {
//            @Override
//            public String apply(String s) throws Exception {
//                if(s.equals("123")){
//                    return s;
//                }
//                return null;
//            }
//        }).subscribe(new Observer<String>() {
//            @Override
//            public void onSubscribe(Disposable d) {
//                System.out.println("onSubscribe");
//                System.out.println(d.toString());
//            }
//
//            @Override
//            public void onNext(String s) {
//                System.out.println("onNext");
//                System.out.println(s);
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                System.out.println("onError");
//                System.out.println(e.getMessage());
//            }
//
//            @Override
//            public void onComplete() {
//                System.out.println("结束");
//            }
//        });
    }
}