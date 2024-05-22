package ru.maximyasn;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Task1 {

  private static final int TIMEOUT = 1;

  private final ExecutorService threadPool = Executors.newFixedThreadPool(2);

  public static void main(String[] args) {
    Task1 task1 = new Task1();
    long start = System.currentTimeMillis();

    ProfileInfo profileInfo = task1.getProfileInfoCompletableFuture(1L);
//    ProfileInfo profileInfo = task1.getProfileInfoExecutorService(1L);

    System.out.println(System.currentTimeMillis() - start);
    System.out.println(profileInfo);

    task1.shutdown();
  }

  // методы

  //метод должен выполниться за 1 секунду
  public ProfileInfo getProfileInfoCompletableFuture(Long id) {

    CompletableFuture<UserInfo> userInfoFuture = CompletableFuture
        .supplyAsync(() -> getUserInfo(id))
        .orTimeout(TIMEOUT, TimeUnit.SECONDS)
        .exceptionally(e -> {
            System.err.println("Exception " + e);
            return new UserInfo("Default", "Default");
        });

    CompletableFuture<CompanyInfo> companyInfoFuture = CompletableFuture
        .supplyAsync(() -> getCompanyInfo(id))
        .orTimeout(TIMEOUT, TimeUnit.SECONDS)
        .exceptionally(e -> {
            System.err.println("Exception " + e);
            return new CompanyInfo("Default", "Default");
        });

    CompletableFuture<ProfileInfo> profileInfo = userInfoFuture
        .thenCombine(companyInfoFuture, ProfileInfo::new);

    return profileInfo.join();
  }

  public ProfileInfo getProfileInfoExecutorService(Long id) {

    Future<UserInfo> userInfoFuture = threadPool.submit(() -> getUserInfo(id));
    Future<CompanyInfo> companyInfoFuture = threadPool.submit(() -> getCompanyInfo(id));

    UserInfo userInfo = null;
    CompanyInfo companyInfo = null;

    try {
      userInfo = userInfoFuture.get(TIMEOUT, TimeUnit.SECONDS);
      companyInfo = companyInfoFuture.get(TIMEOUT, TimeUnit.SECONDS);
    } catch (Exception e) {
      System.err.println("Exception " + e);
    }

    return new ProfileInfo(userInfo, companyInfo);

  }

  public void shutdown() {
    threadPool.shutdown();
  }

  private UserInfo getUserInfo(Long id) {
    try {
      Thread.sleep(900);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    return new UserInfo("test", "test");
  }

  private CompanyInfo getCompanyInfo(Long id) {
    try {
      Thread.sleep(900);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    return new CompanyInfo("test", "test");
  }

  // Модели
  public static class ProfileInfo {

    public UserInfo userInfo;

    public CompanyInfo companyInfo;

    public ProfileInfo(UserInfo userInfo, CompanyInfo companyInfo) {
      this.userInfo = userInfo;
      this.companyInfo = companyInfo;
    }

    @Override
    public String toString() {
      final StringBuffer sb = new StringBuffer("ProfileInfo{");
      sb.append("userInfo=").append(userInfo);
      sb.append(", companyInfo=").append(companyInfo);
      sb.append('}');
      return sb.toString();
    }
  }

  public static class UserInfo {

    public String name;

    public String age;

    public UserInfo(String name, String age) {
      this.name = name;
      this.age = age;
    }

    @Override
    public String toString() {
      final StringBuffer sb = new StringBuffer("UserInfo{");
      sb.append("name='").append(name).append('\'');
      sb.append(", age='").append(age).append('\'');
      sb.append('}');
      return sb.toString();
    }
  }

  public static class CompanyInfo {

    public String id;

    public String companyName;

    public CompanyInfo(String id, String companyName) {
      this.id = id;
      this.companyName = companyName;
    }

    @Override
    public String toString() {
      final StringBuffer sb = new StringBuffer("CompanyInfo{");
      sb.append("id='").append(id).append('\'');
      sb.append(", companyName='").append(companyName).append('\'');
      sb.append('}');
      return sb.toString();
    }
  }

}
