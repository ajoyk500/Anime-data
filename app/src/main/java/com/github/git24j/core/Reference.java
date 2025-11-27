package com.github.git24j.core;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import static com.github.git24j.core.GitException.ErrorCode;

public class Reference extends CAutoReleasable {
    static native int jniCmp(long ref1Ptr, long ref2Ptr);
    static native int jniCreate(
            AtomicLong outRef, long repoPtr, String name, Oid oid, int force, String logMessage);
    static native int jniCreateMatching(
            AtomicLong outRef,
            long repoPtr,
            String name,
            Oid oid,
            int force,
            Oid currentId,
            String logMessage);
    static native int jniDelete(long refPtr);
    static native int jniDup(AtomicLong outDest, long sourcePtr);
    static native int jniDwim(AtomicLong outRef, long repoPtr, String shorthand);
    static native int jniEnsureLog(long repoPtr, String refname);
    static native int jniForeach(long repoPtr, ForeachCb consumer);
    static native int jniForeachGlob(long repoPtr, String glob, ForeachNameCb callback);
    static native int jniForeachName(long repoPtr, ForeachNameCb consumer);
    static native void jniFree(long refPtr);
    static native int jniHasLog(long repoPtr, String refname);
    static native int jniIsBranch(long refPtr);
    static native int jniIsNote(long refPtr);
    static native int jniIsRemote(long refPtr);
    static native int jniIsTag(long refPtr);
    static native int jniIsValidName(String refname);
    static native void jniIteratorFree(long iterPtr);
    static native int jniIteratorGlobNew(AtomicLong outIter, long repoPtr, String glob);
    static native int jniIteratorNew(AtomicLong outIter, long repoPtr);
    static native int jniList(List<String> strList, long repoPtr);
    static native int jniLookup(AtomicLong outRef, long repoPtr, String name);
    static native String jniName(long refPtr);
    static native int jniNameToId(Oid oid, long repoPtr, String name);
    static native int jniNext(AtomicLong outRef, long iterPtr);
    static native int jniNextName(AtomicReference<String> outName, long iterPtr);
    static native int jniNormalizeName(AtomicReference<String> outName, String name, int flags);
    static native long jniOwner(long refPtr);
    static native int jniPeel(AtomicLong outObj, long refPtr, int objType);
    static native int jniRemove(long repoPtr, String name);
    static native int jniRename(
            AtomicLong outRef, long refPtr, String newName, int force, String logMessage);
    static native int jniResolve(AtomicLong outRef, long refPtr);
    static native int jniSetTarget(AtomicLong outRef, long refPtr, Oid oid, String logMessage);
    static native String jniShorthand(long refPtr);
    static native int jniSymbolicCreate(
            AtomicLong outRef,
            long repoPtr,
            String name,
            String target,
            int force,
            String logMessage);
    static native int jniSymbolicCreateMatching(
            AtomicLong outRef,
            long repoPtr,
            String name,
            String target,
            int force,
            String currentValue,
            String logMessage);
    static native int jniSymbolicSetTarget(
            AtomicLong outRef, long refPtr, String target, String logMessage);
    static native String jniSymbolicTarget(long refPtr);
    static native byte[] jniTarget(long refPtr);
    static native byte[] jniTargetPeel(long refPtr);
    static native int jniType(long refPtr);
    protected Reference(boolean isWeak, long rawPtr) {
        super(isWeak, rawPtr);
    }
    @CheckForNull
    public static Reference lookup(@Nonnull Repository repo, @Nonnull String name) {
        AtomicLong outRef = new AtomicLong();
        int e = jniLookup(outRef, repo.getRawPointer(), name);
        if (ErrorCode.of(e) == ErrorCode.ENOTFOUND) {
            return null;
        }
        Error.throwIfNeeded(e);
        return new Reference(false, outRef.get());
    }
    @CheckForNull
    public static Oid nameToId(@Nonnull Repository repo, @Nonnull String name) {
        Oid oid = new Oid();
        int e = jniNameToId(oid, repo.getRawPointer(), name);
        if (ErrorCode.of(e) == ErrorCode.ENOTFOUND) {
            return null;
        }
        Error.throwIfNeeded(e);
        return oid;
    }
    @CheckForNull
    public static Reference dwim(@Nonnull Repository repo, @Nonnull String shorthand) {
        AtomicLong outRef = new AtomicLong();
        int e = jniDwim(outRef, repo.getRawPointer(), shorthand);
        if (e == ErrorCode.ENOTFOUND.getCode()) {
            return null;
        }
        Error.throwIfNeeded(e);
        return new Reference(false, outRef.get());
    }
    @Nonnull
    public static Reference symbolicCreateMatching(
            @Nonnull Repository repo,
            @Nonnull String name,
            @Nonnull String target,
            boolean force,
            String currentValue,
            String logMessage) {
        AtomicLong outRef = new AtomicLong();
        Error.throwIfNeeded(
                jniSymbolicCreateMatching(
                        outRef,
                        repo.getRawPointer(),
                        name,
                        target,
                        force ? 1 : 0,
                        currentValue,
                        logMessage));
        return new Reference(false, outRef.get());
    }
    @Nonnull
    public static Reference symbolicCreate(
            @Nonnull Repository repo,
            @Nonnull String name,
            @Nonnull String target,
            boolean force,
            String logMessage) {
        AtomicLong outRef = new AtomicLong();
        Error.throwIfNeeded(
                jniSymbolicCreate(
                        outRef, repo.getRawPointer(), name, target, force ? 1 : 0, logMessage));
        return new Reference(false, outRef.get());
    }
    @Nonnull
    public static Reference create(
            @Nonnull Repository repo,
            @Nonnull String name,
            @Nonnull Oid oid,
            boolean force,
            String logMessage) {
        AtomicLong outRef = new AtomicLong();
        Error.throwIfNeeded(
                jniCreate(outRef, repo.getRawPointer(), name, oid, force ? 1 : 0, logMessage));
        return new Reference(false, outRef.get());
    }
    @Nonnull
    public static Reference createMatching(
            @Nonnull Repository repo,
            @Nonnull String name,
            @Nonnull Oid oid,
            boolean force,
            Oid currentId,
            String logMessage) {
        AtomicLong outRef = new AtomicLong();
        Error.throwIfNeeded(
                jniCreateMatching(
                        outRef,
                        repo.getRawPointer(),
                        name,
                        oid,
                        force ? 1 : 0,
                        currentId,
                        logMessage));
        return new Reference(false, outRef.get());
    }
    public static void delete(Reference ref) {
        if (ref != null && ref.getRawPointer() != 0) {
            Error.throwIfNeeded(jniDelete(ref.getRawPointer()));
        }
    }
    public static void remove(Repository repo, String name) {
        Error.throwIfNeeded(jniRemove(repo.getRawPointer(), name));
    }
    @Nonnull
    public static List<String> list(@Nonnull Repository repo) {
        List<String> strList = new ArrayList<>();
        Error.throwIfNeeded(jniList(strList, repo.getRawPointer()));
        return strList;
    }
    public static void foreach(
            @Nonnull Repository repo, @Nonnull Function<Reference, Integer> callback) {
        ForeachCb cb = ptr -> callback.apply(new Reference(true, ptr));
        int e = jniForeach(repo.getRawPointer(), cb);
        Error.throwIfNeeded(e);
    }
    public static void foreachName(
            @Nonnull Repository repo, @Nonnull Function<String, Integer> callback) {
        Error.throwIfNeeded(jniForeachName(repo.getRawPointer(), callback::apply));
    }
    public static int cmp(@Nullable Reference ref1, @Nullable Reference ref2) {
        return jniCmp(
                ref1 == null ? 0 : ref1.getRawPointer(), ref2 == null ? 0 : ref2.getRawPointer());
    }
    @Nonnull
    public static Iterator iteratorGlobNew(@Nonnull Repository repo, @Nonnull String glob) {
        AtomicLong outIter = new AtomicLong();
        Error.throwIfNeeded(jniIteratorGlobNew(outIter, repo.getRawPointer(), glob));
        return new Iterator(false, outIter.get());
    }
    @CheckForNull
    public static Reference next(@Nonnull Iterator iter) {
        AtomicLong outRef = new AtomicLong();
        int e = jniNext(outRef, iter.getRawPointer());
        if (e == ErrorCode.ITEROVER.getCode()) {
            return null;
        }
        Error.throwIfNeeded(e);
        return new Reference(true, outRef.get());
    }
    public static void foreachGlob(
            @Nonnull Repository repo, @Nonnull String glob, @Nonnull ForeachNameCb callback) {
        Error.throwIfNeeded(jniForeachGlob(repo.getRawPointer(), glob, callback));
    }
    public static boolean hasLog(@Nonnull Repository repo, @Nonnull String refname) {
        int e = jniHasLog((repo.getRawPointer()), refname);
        if (e == 0 || e == 1) {
            return e == 1;
        }
        Error.throwIfNeeded(e);
        return false;
    }
    public static void ensureLog(Repository repo, String refname) {
        Error.throwIfNeeded(jniEnsureLog(repo.getRawPointer(), refname));
    }
    public static String normalizeName(String name, EnumSet<Format> flags) {
        AtomicReference<String> outName = new AtomicReference<>();
        Error.throwIfNeeded(jniNormalizeName(outName, name, IBitEnum.bitOrAll(flags)));
        return outName.get();
    }
    public static boolean isValidName(String refname) {
        return jniIsValidName(refname) == 1;
    }
    @Nonnull
    public static Iterator iteratorNew(@Nonnull Repository repo) {
        AtomicLong outIter = new AtomicLong();
        Error.throwIfNeeded(jniIteratorNew(outIter, repo.getRawPointer()));
        return new Iterator(false, outIter.get());
    }
    @CheckForNull
    public static String nextName(@Nonnull Iterator iterator) {
        AtomicReference<String> outName = new AtomicReference<>();
        int e = jniNextName(outName, iterator.getRawPointer());
        if (ErrorCode.ITEROVER.getCode() == e) {
            return null;
        }
        Error.throwIfNeeded(e);
        return outName.get();
    }
    @Override
    protected void freeOnce(long cPtr) {
        jniFree(cPtr);
    }
    @CheckForNull
    public Oid targetPeel() {
        return Oid.ofNullable(jniTargetPeel(this.getRawPointer()));
    }
    @CheckForNull
    public String symbolicTarget() {
        return jniSymbolicTarget(this.getRawPointer());
    }
    @Nonnull
    public ReferenceType type() {
        return ReferenceType.valueOf(jniType(this.getRawPointer()));
    }
    @Nonnull
    public String name() {
        return jniName(this.getRawPointer());
    }
    @CheckForNull
    public Reference resolve() {
        AtomicLong outRef = new AtomicLong();
        Error.throwIfNeeded(jniResolve(outRef, getRawPointer()));
        return outRef.get() != 0 ? new Reference(false, outRef.get()) : null;
    }
    @Nonnull
    public Repository owner() {
        return new Repository(jniOwner(getRawPointer()));
    }
    @CheckForNull
    public Reference symbolicSetTarget(@Nonnull String target, @Nonnull String logMessage) {
        AtomicLong outRef = new AtomicLong();
        Error.throwIfNeeded(jniSymbolicSetTarget(outRef, getRawPointer(), target, logMessage));
        return outRef.get() != 0 ? new Reference(false, outRef.get()) : null;
    }
    @Nonnull
    public Reference setTarget(@Nonnull Oid oid, @Nonnull String logMessage) {
        AtomicLong outRef = new AtomicLong();
        Error.throwIfNeeded(jniSetTarget(outRef, getRawPointer(), oid, logMessage));
        return new Reference(false, outRef.get());
    }
    @Nonnull
    public Reference rename(@Nonnull String newName, boolean force, String logMessage) {
        AtomicLong outRef = new AtomicLong();
        Error.throwIfNeeded(jniRename(outRef, getRawPointer(), newName, force ? 1 : 0, logMessage));
        return new Reference(false, outRef.get());
    }
    @Nonnull
    public Reference dup() {
        AtomicLong outRef = new AtomicLong();
        Error.throwIfNeeded(jniDup(outRef, this.getRawPointer()));
        return new Reference(false, outRef.get());
    }
    public boolean isBranch() {
        return jniIsBranch(getRawPointer()) == 1;
    }
    public boolean isRemote() {
        return jniIsRemote(getRawPointer()) == 1;
    }
    public boolean isTag() {
        return jniIsTag(getRawPointer()) == 1;
    }
    public boolean isNote() {
        return jniIsNote(getRawPointer()) == 1;
    }
    public GitObject peel(GitObject.Type objType) {
        GitObject out = new GitObject(false, 0);
        int e = jniPeel(out._rawPtr, this._rawPtr.get(), objType.getBit());
        Error.throwIfNeeded(e);
        return out;
    }
    @Nonnull
    public String shorthand() {
        return jniShorthand(getRawPointer());
    }
    @CheckForNull
    public Oid target() {
        return Oid.ofNullable(jniTarget(getRawPointer()));
    }
    @CheckForNull
    public Oid id() {
        return target();
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Reference reference = (Reference) o;
        return Reference.cmp(reference, this) == 0;
    }
    @Override
    public int hashCode() {
        return Objects.hashCode(_rawPtr);
    }
    public enum Format implements IBitEnum {
        NORMAL(0),
        ALLOW_ONELEVEL(1 << 0),
        REFSPEC_PATTERN(1 << 1),
        REFSPEC_SHORTHAND(1 << 2),
        ;
        private final int _bit;
        Format(int bit) {
            this._bit = bit;
        }
        @Override
        public int getBit() {
            return _bit;
        }
    }
    public enum ReferenceType implements IBitEnum {
        INVALID(0),
        DIRECT(1),
        SYMBOLIC(2),
        ALL(3);
        private final int _bit;
        ReferenceType(int bit) {
            _bit = bit;
        }
        static ReferenceType valueOf(int iVal) {
            return IBitEnum.valueOf(iVal, ReferenceType.class, INVALID);
        }
        @Override
        public int getBit() {
            return _bit;
        }
    }
    @FunctionalInterface
    public interface ForeachNameCb {
        int accept(String name);
    }
    @FunctionalInterface
    public interface ForeachCb {
        int accept(long refPtr);
    }
    public static class Iterator extends CAutoReleasable {
        protected Iterator(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }
        @Override
        protected void freeOnce(long cPtr) {
            jniIteratorFree(cPtr);
        }
    }
}
