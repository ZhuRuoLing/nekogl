package icu.takeneko.nekogl.call;

import icu.takeneko.nekogl.GlContext;

public interface RenderCall {
    void accept(GlContext context) throws Throwable;
}
