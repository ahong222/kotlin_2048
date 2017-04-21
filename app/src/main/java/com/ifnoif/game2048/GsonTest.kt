package com.ifnoif.game2048

import com.google.gson.Gson
import org.json.JSONObject
import java.io.FileOutputStream
import java.lang.reflect.Field

/**
 * Created by shen on 17/4/21.
 */

class GsonTest {

    data class Info(var name: String, var age: Int) {

    }


    companion object Singlon{
        var name:String?= null;
        @Throws(Exception::class)
        fun testGson() {
            var jsonObject: JSONObject = JSONObject()
            jsonObject.put("name", "People1")
            jsonObject.put("age", 20)

            var info: Info = Gson().fromJson(jsonObject.toString(), Info::class.java)

            println("info:" + info + " class:" + GsonTest.javaClass + " class2:" + (GsonTest::class.java))
            //info:Info(name=People1, age=20) class:class com.ifnoif.game2048.GsonTest$Companion class2:class com.ifnoif.game2048.GsonTest

            //测试反射
            /**
             * 输出
             * info field:age value:20
             * info field:name value:People1
             */
            var javaClass: Class<Info> = Info::class.java
            var fields: Array<Field> = javaClass.declaredFields
            fields.forEach { field -> field.isAccessible = true; println("info field:" + field.name + " value:" + field.get(info)) }

            var y: Any = "123"
            val x: String? = y as? String

            println("x:"+x+" y:"+y+" result:"+(x==y)+" equals:"+x?.equals(y))

            var a:Int = 10;
            var b = a as Int;

            println("a:"+a+" b:"+b)
        }


    }


}