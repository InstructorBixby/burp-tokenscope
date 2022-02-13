package net.c01n31.tokenscope;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TokenStoreTest {

    @Test
    public void sameValueIsOneTokenWithACount() {
        TokenStore store = new TokenStore();
        store.record("a.example.com", "SESSION", "abc", 1L);
        store.record("b.example.com", "SESSION", "abc", 2L);
        assertEquals(1, store.size());
        assertEquals(2, store.find("abc").get().count());
    }

    @Test
    public void firstHostIsKept() {
        TokenStore store = new TokenStore();
        store.record("a.example.com", "SESSION", "abc", 1L);
        store.record("b.example.com", "SESSION", "abc", 2L);
        assertEquals("a.example.com", store.find("abc").get().host());
    }

    @Test
    public void boundedByMax() {
        TokenStore store = new TokenStore(3);
        for (int i = 0; i < 10; i++) {
            store.record("example.com", "CSRF", "t" + i, i);
        }
        assertEquals(3, store.size());
        assertFalse(store.find("t0").isPresent());
        assertTrue(store.find("t9").isPresent());
    }

    @Test
    public void clearEmptiesIt() {
        TokenStore store = new TokenStore();
        store.record("example.com", "SESSION", "abc", 1L);
        store.clear();
        assertEquals(0, store.size());
    }
}
