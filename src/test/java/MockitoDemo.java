import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.InOrder;

import java.util.LinkedList;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.*;

// 静态导入会使代码更简洁

public class MockitoDemo {

    // 验证某些行为
    @Test
    public void verify_behaviour() {
        // mock creation 创建mock对象
        List mockedList = mock(List.class);

        // using mock object 使用mock对象
        mockedList.add("one");
        mockedList.clear();

        // verification 验证
        // 一旦mock对象被创建了，mock对象会记住所有的交互，然后你就可以选择性的验证你感兴趣的交互。
        verify(mockedList).add("one");
        verify(mockedList).clear();
    }

    // 如何做一些测试桩stub
    @Test(expected = RuntimeException.class)
    public void when_thenReturn() {
        // 你可以mock具体的类型，不仅是接口
        LinkedList mockedList = mock(LinkedList.class);

        // 测试桩
        when(mockedList.get(0)).thenReturn("first");
        when(mockedList.get(1)).thenThrow(new RuntimeException());

        // 输出first
        System.out.println(mockedList.get(0));

        // 抛出异常
        System.out.println(mockedList.get(1));

        // 因为get(999)没有打桩，因此输出null
        System.out.println(mockedList.get(999));

        // 验证get(0)被调用的次数
        // Although it is possible to verify a stubbed invocation,usually it's just redundant
        // If your code cares what get(0) returns then something else breaks (often before even verify() gets executed).
        // If your code doesn't care what get(0) returns then it should not be stubbed. Not convinced? See here.
        verify(mockedList).get(0);

        /*
        默认情况下，所有的函数都有返回值。mock函数默认返回的是null，一个空的集合或者一个被对象类型包装的内置类型，例如0、false对应的对象类型为Integer、Boolean；
        测试桩函数可以被覆写 : 例如常见的测试桩函数可以用于初始化夹具，但是测试函数能够覆写它。请注意，覆写测试桩函数是一种可能存在潜在问题的做法；
        一旦测试桩函数被调用，该函数将会一致返回固定的值；
        上一次调用测试桩函数有时候极为重要-当你调用一个函数很多次时，最后一次调用可能是你所感兴趣的。
        在测试桩中预设get(1) 跑出 RuntimeException 异常； 在@Test() 中设置 expected = RuntimeException.class ，说明该异常为测试用例预期结果。
        */
    }

    // 参数匹配器matchers
    @Test
    public void with_arguments() {
        // Mockito以自然的java风格来验证参数值：使用equals()函数。有时，当需要额外的灵活性时你可能需要使用参数匹配器，也就是argument matchers
        LinkedList mockedList = mock(LinkedList.class);

        // 使用内置的anyInt()参数匹配器
        when(mockedList.get(anyInt())).thenReturn("element");

        // 输出element
        //System.out.println(mockedList.get(1));
        System.out.println(mockedList.get(50));

        // 你也可以验证参数匹配器
        verify(mockedList).get(anyInt());
    }

    // 自定义参数匹配器
    @Test
    public void with_arguments2() {
        LinkedList mockedList = mock(LinkedList.class);

        // 使用自定义的参数匹配器(在IsValid()类中返回你自己的匹配器实现)
        when(mockedList.contains(argThat(new IsValid()))).thenReturn(true);

        assertTrue(mockedList.contains(1));
        assertTrue(!mockedList.contains(3));
    }

    // 自己定义 IsValid 类，处理不同参数时的返回
    private class IsValid extends ArgumentMatcher<List> {
        @Override
        public boolean matches(Object o) {
            return o.equals(1) || o.equals(2);
        }
    }

    // 验证函数调用次数
    @Test
    public void verifying_number_of_invocations() {
        LinkedList mockedList = mock(LinkedList.class);

        mockedList.add("once");

        mockedList.add("twice");
        mockedList.add("twice");

        mockedList.add("three times");
        mockedList.add("three times");
        mockedList.add("three times");

        // 下面的两个验证函数效果一样,因为verify默认验证的就是times(1)
        verify(mockedList).add("once");
        verify(mockedList, times(1)).add("once");

        // 验证具体的执行次数
        verify(mockedList, times(2)).add("twice");
        verify(mockedList, times(3)).add("three times");

        // 使用never()进行验证,never相当于times(0)
        verify(mockedList, never()).add("never happened");

        // 使用atLeast()/atMost()
        verify(mockedList, atLeastOnce()).add("three times");
        verify(mockedList, atLeast(0)).add("five times");
        verify(mockedList, atMost(5)).add("three times");

        // verify函数默认验证的是执行了times(1)，也就是某个测试函数是否执行了1次.因此，times(1)通常被省略了。
        // atLeastOnce() 验证至少调用一次
        // atLeast(2) 验证至少调用2次
        // atMost(5) 验证至多调用3次
    }

    // 验证执行顺序
    @Test
    public void verification_in_order() {
        // A.验证mock一个对象的函数执行顺序
        List singleMock = mock(List.class);

        singleMock.add("was added first");
        singleMock.add("was added second");

        // 为该mock对象创建一个inOrder对象
        InOrder inOrder = inOrder(singleMock);

        // 确保add函数首先执行的是add("was added first"),然后才是add("was added second")
        inOrder.verify(singleMock).add("was added first");
        inOrder.verify(singleMock).add("was added second");

        // B.验证多个mock对象的函数执行顺序
        List firstMock = mock(List.class);
        List secondMock = mock(List.class);

        firstMock.add("was called first");
        secondMock.add("was called second");

        // 为这两个mock对象创建inOrder对象
        InOrder inOrder2 = inOrder(firstMock, secondMock);

        // 验证他们的执行顺序
        inOrder2.verify(firstMock).add("was called first");
        inOrder2.verify(secondMock).add("was called second");

        // 这里对验证的顺序有着严格的要求，如果将inOrder.verify(singleMock).add("was added first");
        // 和 inOrder.verify(singleMock).add("was added second");上下对调一下位置，则验证失败。
    }

    // 通过when(Object)为无返回值的函数打桩有不同的方法,因为编译器不喜欢void函数在括号内
    // 使用doThrow(Throwable) 替换stubVoid(Object)来为void函数打桩是为了与doAnswer()等函数族保持一致性
    // 当你想为void函数打桩时使用含有一个exception 参数的doAnswer()
    @Test(expected = RuntimeException.class)
    public void when_doThrow() throws RuntimeException {
        LinkedList mockedList = mock(LinkedList.class);
        doThrow(new RuntimeException()).when(mockedList).clear();

        // 下面的代码会抛出异常
        mockedList.clear();

        // 当你调用doThrow(), doAnswer(), doNothing(), doReturn() 和 doCallRealMethod() 这些函数时
        // 可以在适当的位置调用when()函数,例如下面这些功能时这是必须的:
        // 1.测试void函数 2.在受监控的对象上测试函数 3.不止一次的测试为同一个函数，在测试过程中改变mock对象的行为
        // 但是在调用when()函数时你可以选择是否调用这些上述函数
    }

}
