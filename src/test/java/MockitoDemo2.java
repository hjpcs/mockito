import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class MockitoDemo2 {

    // 在前面的测试中我们在每个测试方法里都mock了一个List对象，为了避免重复的mock
    // 使测试类更具有可读性，我们可以使用下面的注解方式来快速模拟对象

    @Mock
    private List mockList;

    // 必须在基类中添加初始化mock的代码，否则运行测试类会报NullPointerException
    // 或者使用built-in runner：MockitoJUnitRunner
    /*public MockitoDemo2() {
        MockitoAnnotations.initMocks(this);
    }*/

    @Test
    public void shorthand() {
        mockList.add(1);
        verify(mockList).add(1);
    }
}
