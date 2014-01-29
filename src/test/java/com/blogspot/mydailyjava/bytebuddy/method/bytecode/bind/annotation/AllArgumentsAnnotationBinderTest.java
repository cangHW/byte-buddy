package com.blogspot.mydailyjava.bytebuddy.method.bytecode.bind.annotation;

import com.blogspot.mydailyjava.bytebuddy.method.bytecode.assign.IllegalAssignment;
import com.blogspot.mydailyjava.bytebuddy.method.bytecode.assign.LegalTrivialAssignment;
import org.junit.Test;

import java.lang.annotation.Annotation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.*;

public class AllArgumentsAnnotationBinderTest extends AbstractAnnotationBinderTest<AllArguments> {

    public AllArgumentsAnnotationBinderTest() {
        super(AllArguments.class);
    }

    @Test
    public void testAnnotationType() throws Exception {
        assertEquals(AllArguments.class, AllArguments.Binder.INSTANCE.getHandledType());
    }

    @Test
    public void testLegalBindingNoRuntimeType() throws Exception {
        final int targetIndex = 1;
        when(assigner.assign(any(Class.class), any(Class.class), anyBoolean())).thenReturn(LegalTrivialAssignment.INSTANCE);
        when(source.getParameterTypes()).thenReturn(new Class<?>[]{int.class, Object.class});
        when(source.isStatic()).thenReturn(false);
        when(target.getParameterTypes()).thenReturn(new Class<?>[]{null, Void[].class});
        when(target.getParameterAnnotations()).thenReturn(new Annotation[targetIndex + 1][0]);
        AnnotationDrivenBinder.ArgumentBinder.IdentifiedBinding<?> identifiedBinding = AllArguments.Binder.INSTANCE
                .bind(annotation, targetIndex, source, target, typeDescription, assigner);
        assertThat(identifiedBinding.isValid(), is(true));
        verify(source, atLeast(1)).getParameterTypes();
        verify(source, atLeast(1)).isStatic();
        verify(target, atLeast(1)).getParameterTypes();
        verify(target, atLeast(1)).getParameterAnnotations();
        verify(assigner).assign(int.class, Void.class, false);
        verify(assigner).assign(Object.class, Void.class, false);
        verifyNoMoreInteractions(assigner);
    }

    @Test
    public void testLegalBindingRuntimeType() throws Exception {
        final int targetIndex = 1;
        when(assigner.assign(any(Class.class), any(Class.class), anyBoolean())).thenReturn(LegalTrivialAssignment.INSTANCE);
        when(source.getParameterTypes()).thenReturn(new Class<?>[]{int.class, Object.class});
        when(source.isStatic()).thenReturn(false);
        when(target.getParameterTypes()).thenReturn(new Class<?>[]{null, Void[].class});
        RuntimeType runtimeType = mock(RuntimeType.class);
        doReturn(RuntimeType.class).when(runtimeType).annotationType();
        when(target.getParameterAnnotations()).thenReturn(new Annotation[][]{{}, {runtimeType}});
        AnnotationDrivenBinder.ArgumentBinder.IdentifiedBinding<?> identifiedBinding = AllArguments.Binder.INSTANCE
                .bind(annotation, targetIndex, source, target, typeDescription, assigner);
        assertThat(identifiedBinding.isValid(), is(true));
        verify(source, atLeast(1)).getParameterTypes();
        verify(source, atLeast(1)).isStatic();
        verify(target, atLeast(1)).getParameterTypes();
        verify(target, atLeast(1)).getParameterAnnotations();
        verify(assigner).assign(int.class, Void.class, true);
        verify(assigner).assign(Object.class, Void.class, true);
        verifyNoMoreInteractions(assigner);
    }

    @Test
    public void testIllegalBinding() throws Exception {
        final int targetIndex = 0;
        when(source.getParameterTypes()).thenReturn(new Class<?>[] {Object.class});
        when(target.getParameterTypes()).thenReturn(new Class<?>[] {Void[].class});
        when(target.getParameterAnnotations()).thenReturn(new Annotation[2][0]);
        when(assigner.assign(any(Class.class), any(Class.class), anyBoolean())).thenReturn(IllegalAssignment.INSTANCE);
        AnnotationDrivenBinder.ArgumentBinder.IdentifiedBinding<?> identifiedBinding = AllArguments.Binder.INSTANCE
                .bind(annotation, targetIndex, source, target, typeDescription, assigner);
        assertThat(identifiedBinding.isValid(), is(false));
        verify(source, atLeast(1)).getParameterTypes();
        verify(target, atLeast(1)).getParameterTypes();
        verify(assigner).assign(Object.class, Void.class, false);
        verifyNoMoreInteractions(assigner);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNonArrayTypeBinding() throws Exception {
        when(target.getParameterTypes()).thenReturn(new Class<?>[] {Object.class});
        AllArguments.Binder.INSTANCE.bind(annotation, 0, source, target, typeDescription, assigner);
    }
}
