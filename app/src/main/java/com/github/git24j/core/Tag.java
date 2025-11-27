package com.github.git24j.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Tag extends GitObject {
    static native int jniAnnotationCreate(
            Oid oid, long repoPtr, String tagName, long target, long tagger, String message);
    static native int jniCreate(
            Oid oid,
            long repoPtr,
            String tagName,
            long target,
            long tagger,
            String message,
            int force);
    static native int jniCreateFromBuffer(Oid oid, long repoPtr, String buffer, int force);
    static native int jniCreateLightWeight(
            Oid oid, long repoPtr, String tagName, long targetPtr, int force);
    static native int jniDelete(long repoPtr, String tagName);
    static native int jniForeach(long repoPtr, ForeachCb callback);
    static native int jniList(List<String> tagNames, long repoPtr);
    static native int jniListMatch(List<String> tagNames, String pattern, long repoPtr);
    static native String jniMessage(long tagPtr);
    static native String jniName(long tagPtr);
    static native int jniPeel(AtomicLong outTarget, long tagPtr);
    static native long jniTagger(long tagPtr);
    static native int jniTarget(AtomicLong outTargetPtr, long tagPtr);
    static native byte[] jniTargetId(long tagPtr);
    static native int jniTargetType(long tagPtr);
    Tag(boolean weak, long rawPointer) {
        super(weak, rawPointer);
    }
    public static Tag lookup(Repository repo, Oid oid) {
        return (Tag) GitObject.lookup(repo, oid, Type.TAG);
    }
    public static Tag lookupPrefix(Repository repo, String shortId) {
        return (Tag) GitObject.lookupPrefix(repo, shortId, Type.TAG);
    }
    public static Oid create(
            @Nonnull Repository repo,
            @Nonnull String tagName,
            @Nonnull GitObject target,
            @Nonnull Signature tagger,
            @Nonnull String message,
            boolean force) {
        Oid outOid = new Oid();
        int e =
                jniCreate(
                        outOid,
                        repo.getRawPointer(),
                        tagName,
                        target.getRawPointer(),
                        tagger.getRawPointer(),
                        message,
                        force ? 1 : 0);
        Error.throwIfNeeded(e);
        return outOid;
    }
    @Nonnull
    public static Oid annotationCreate(
            @Nonnull Repository repo,
            @Nonnull String tagName,
            @Nonnull GitObject target,
            @Nonnull Signature tagger,
            @Nonnull String message) {
        Oid outOid = new Oid();
        int e =
                jniAnnotationCreate(
                        outOid,
                        repo.getRawPointer(),
                        tagName,
                        target.getRawPointer(),
                        tagger.getRawPointer(),
                        message);
        Error.throwIfNeeded(e);
        return outOid;
    }
    public static void delete(Repository repo, String tagName) {
        Error.throwIfNeeded(jniDelete(repo.getRawPointer(), tagName));
    }
    public static List<String> list(Repository repo) {
        List<String> tagNames = new ArrayList<>();
        Error.throwIfNeeded(jniList(tagNames, repo.getRawPointer()));
        return tagNames;
    }
    public static List<String> listMatch(String pattern, Repository repo) {
        List<String> tagNames = new ArrayList<>();
        Error.throwIfNeeded(jniListMatch(tagNames, pattern, repo.getRawPointer()));
        return tagNames;
    }
    public static void foreach(Repository repo, ForeachCb callback) {
        Error.throwIfNeeded(jniForeach(repo.getRawPointer(), callback));
    }
    public static Oid createFromBuffer(Repository repo, String buffer, boolean force) {
        Oid outOid = new Oid();
        Error.throwIfNeeded(
                jniCreateFromBuffer(outOid, repo.getRawPointer(), buffer, force ? 1 : 0));
        return outOid;
    }
    public static Oid createLightWeight(
            Repository repo, String tagName, GitObject target, boolean force) {
        Oid oid = new Oid();
        Error.throwIfNeeded(
                jniCreateLightWeight(
                        oid, repo.getRawPointer(), tagName, target.getRawPointer(), force ? 1 : 0));
        return oid;
    }
    @Override
    public Tag dup() {
        Tag tag = new Tag(false, 0);
        GitObject.jniDup(tag._rawPtr, getRawPointer());
        return tag;
    }
    public GitObject target() {
        GitObject out = new GitObject(true, 0);
        Error.throwIfNeeded(jniTarget(out._rawPtr, getRawPointer()));
        return out;
    }
    @CheckForNull
    public Oid targetId() {
        return Oid.ofNullable(jniTargetId(getRawPointer()));
    }
    public Type targetType() {
        return Type.valueOf(jniTargetType(getRawPointer()));
    }
    public String name() {
        return jniName(getRawPointer());
    }
    @Nullable
    public Signature tagger() {
        long ptr = jniTagger(getRawPointer());
        return ptr == 0 ? null : new Signature(true, ptr);
    }
    public String message() {
        return jniMessage(getRawPointer());
    }
    public GitObject peel() {
        GitObject target = new GitObject(true, 0);
        Error.throwIfNeeded(jniPeel(target._rawPtr, getRawPointer()));
        return target;
    }
    @FunctionalInterface
    public interface ForeachCb {
        int accept(String name, String oid);
    }
}
