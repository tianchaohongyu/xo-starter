package com.github.jnoee.xo.jpa.query;

import com.github.jnoee.xo.utils.StringUtils;
import lombok.Getter;
import org.hibernate.query.Query;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.persistence.criteria.CriteriaBuilder.In;
import java.util.*;
import java.util.Map.Entry;

@Getter
public class Criteria<E> {
  private Class<E> entityClass;
  private EntityManager entityManager;
  private CriteriaBuilder criteriaBuilder;
  private CriteriaQuery<E> criteriaQuery;
  private Root<E> root;
  private List<Predicate> predicates = new ArrayList<>();
  private List<Order> orders = new ArrayList<>();
  private Map<String, Criteria<?>> linkCriteria = new HashMap<>();
  private String groupBy;

  public Criteria(Class<E> entityClass, EntityManager entityManager) {
    this.entityClass = entityClass;
    this.entityManager = entityManager;
    criteriaBuilder = this.entityManager.getCriteriaBuilder();
    criteriaQuery = criteriaBuilder.createQuery(entityClass);
    root = criteriaQuery.from(entityClass);
  }

  public TypedQuery<E> toTypedQuery() {
    return entityManager.createQuery(toCriteriaQuery());
  }

  public CriteriaQuery<E> toCriteriaQuery() {
    criteriaQuery.where(predicates.toArray(new Predicate[0]));
    if (StringUtils.isNotBlank(groupBy)) {
      criteriaQuery.groupBy(root.get(groupBy));
    }
    if (orders != null) {
      criteriaQuery.orderBy(orders);
    }
    addLinkPredicates(this);
    return criteriaQuery;
  }

  public Integer count() {
    @SuppressWarnings("unchecked")
    Query<E> query = toTypedQuery().unwrap(Query.class);
    String jpql = query.getQueryString();

    String fromJpql = "from " + StringUtils.substringBetween(jpql, "from", "order by");
    String countJpql = "select count(*) " + fromJpql;

    TypedQuery<Long> countQuery = entityManager.createQuery(countJpql, Long.class);
    for (String paramName : query.getParameterMetadata().getNamedParameterNames()) {
      countQuery.setParameter(paramName, query.getParameterValue(paramName));
    }
    return countQuery.getSingleResult().intValue();
  }

  public void addPredicate(Predicate predicate) {
    predicates.add(predicate);
  }

  public void eq(String propertyName, Object value) {
    addPredicate(criteriaBuilder.equal(root.get(propertyName), value));
  }

  public void eq(List<String> propertyNames, Object value) {
    List<Predicate> eqPredicates = new ArrayList<>();
    for (String propertyName : propertyNames) {
      eqPredicates.add(criteriaBuilder.equal(root.get(propertyName), value));
    }
    addPredicate(criteriaBuilder.or(eqPredicates.toArray(new Predicate[eqPredicates.size()])));
  }

  public void notEq(String propertyName, Object value) {
    addPredicate(criteriaBuilder.notEqual(root.get(propertyName), value));
  }

  public void isNull(String propertyName) {
    addPredicate(criteriaBuilder.isNull(root.get(propertyName)));
  }

  public void isNotNull(String propertyName) {
    addPredicate(criteriaBuilder.isNotNull(root.get(propertyName)));
  }

  public void like(String propertyName, String value) {
    if (value.indexOf('%') < 0) {
      value = "%" + value + "%";
    }
    addPredicate(criteriaBuilder.like(root.get(propertyName), value));
  }

  public void like(List<String> propertyNames, String value) {
    if (value.indexOf('%') < 0) {
      value = "%" + value + "%";
    }
    List<Predicate> likePredicates = new ArrayList<>();
    for (String propertyName : propertyNames) {
      likePredicates.add(criteriaBuilder.like(root.get(propertyName), value));
    }
    addPredicate(criteriaBuilder.or(likePredicates.toArray(new Predicate[likePredicates.size()])));
  }

  public <I> void in(String propertyName, Collection<I> value) {
    In<I> in = criteriaBuilder.in(root.get(propertyName));
    Iterator<I> iterator = value.iterator();
    while (iterator.hasNext()) {
      in.value(iterator.next());
    }
    addPredicate(in);
  }

  public <I> void notIn(String propertyName, Collection<I> value) {
    In<I> in = criteriaBuilder.in(root.get(propertyName));
    Iterator<I> iterator = value.iterator();
    while (iterator.hasNext()) {
      in.value(iterator.next());
    }
    addPredicate(criteriaBuilder.not(in));
  }

  public <Y extends Comparable<? super Y>> void le(String propertyName, Y value) {
    addPredicate(criteriaBuilder.lessThanOrEqualTo(root.get(propertyName), value));
  }

  public  <Y extends Comparable<? super Y>> void lt(String propertyName, Y value) {
    addPredicate(criteriaBuilder.lessThan(root.get(propertyName), value));
  }

  public <Y extends Comparable<? super Y>> void ge(String propertyName, Y value) {
    addPredicate(criteriaBuilder.greaterThanOrEqualTo(root.get(propertyName), value));
  }

  public <Y extends Comparable<? super Y>>  void gt(String propertyName, Y value) {
    addPredicate(criteriaBuilder.greaterThan(root.get(propertyName), value));
  }
  public <Y extends Comparable<? super Y>>  void between(String propertyName, Y lo, Y go) {
    ge(propertyName, lo);
    le(propertyName, go);
  }

  public void asc(String propertyName) {
    orders.add(criteriaBuilder.asc(root.get(propertyName)));
  }

  public void desc(String propertyName) {
    orders.add(criteriaBuilder.desc(root.get(propertyName)));
  }

  private void addLinkPredicates(Criteria<?> criteria) {
    for (Entry<String, Criteria<?>> entry : criteria.linkCriteria.entrySet()) {
      root.join(entry.getKey());
      criteriaQuery.where(entry.getValue().predicates.toArray(new Predicate[0]));
      addLinkPredicates(entry.getValue());
    }
  }
}
