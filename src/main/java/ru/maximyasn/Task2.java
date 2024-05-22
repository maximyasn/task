package ru.maximyasn;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Task2 {

  public static void main(String[] args) {

    List<Profile> profiles = List.of(
        new Profile(1L, 0L, 1L),
        new Profile(2L, 0L, 1L),
        new Profile(3L, 0L, 2L),
        new Profile(4L, 1L, 1L),
        new Profile(5L, 1L, 2L)
    );

    groupByOrgIdAndGroupId(profiles).entrySet().forEach(System.out::println);

  }

  public static Map<Long, Map<Long, List<Profile>>> groupByOrgIdAndGroupId(List<Profile> data) {
    return data
        .stream()
        .collect(Collectors.groupingBy(Profile::getOrgId, Collectors.groupingBy(Profile::getGroupId)));
  }


  static public class Profile {

    private Long id;

    private Long orgId;

    private Long groupId;

    public Profile(Long id, Long orgId, Long groupId) {
      this.id = id;
      this.orgId = orgId;
      this.groupId = groupId;
    }

    public Long getOrgId() {
      return orgId;
    }

    public Long getGroupId() {
      return groupId;
    }

    @Override
    public String toString() {
      return String.valueOf(id);
    }

  }
// Пример (входные данные):

//[1, 0, 1]
//    [2, 0, 1]
//    [3, 0, 2]
//    [4, 1, 1]
//    [5, 1, 2]
//
//  Result (вывод структуры):
//  {
//    "0": {
//      "1" : [{1..}, {2..}],
//      "2" : [{3..}],
//  }
//    "1": {
//      "1" : [{4..}],
//      "2" : [{5..}]
//  }
//  }
}
