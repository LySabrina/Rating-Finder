package com.example.ratingfinder.Repository;

import com.example.ratingfinder.models.Product;
import com.example.ratingfinder.models.ProductPage;
import com.example.ratingfinder.models.ProductSearchCriteria;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Repository
public class ProductCriteriaRepository {

    private final EntityManager entityManager;
    private final CriteriaBuilder criteriaBuilder;

    public ProductCriteriaRepository (EntityManager entityManager){
        this.entityManager = entityManager;
        this.criteriaBuilder = entityManager.getCriteriaBuilder();
    }

    public Page<Product> findProductsWithFilter(ProductPage productPage, ProductSearchCriteria productSearchCriteria){
        CriteriaQuery<Product> criteriaQuery = criteriaBuilder.createQuery(Product.class); //Preparing to make query
        Root<Product> root = criteriaQuery.from(Product.class);
        Predicate predicate = getPredicate(root, productSearchCriteria);


        criteriaQuery.where(predicate);
        TypedQuery<Product> typedQuery = entityManager.createQuery(criteriaQuery);
        typedQuery.setFirstResult(productPage.getPageNumber() * productPage.getPageSize());
        typedQuery.setMaxResults(productPage.getPageSize());

        Pageable pageable = PageRequest.of(productPage.getPageNumber(), productPage.getPageSize());
        long productCount = getProductCount(predicate, productSearchCriteria);
        return new PageImpl<>(typedQuery.getResultList(), pageable, productCount);
    }

    public Predicate getPredicate(Root<Product> root, ProductSearchCriteria productSearchCriteria){
        List<Predicate> predicateList = new ArrayList<>();

            if(Objects.nonNull(productSearchCriteria.getBrands())){

                predicateList.add(root.get("brand").in(productSearchCriteria.getBrands()));
            }

        if(Objects.nonNull(productSearchCriteria.getProduct_type())){

            predicateList.add(root.get("type").in(productSearchCriteria.getProduct_type()));

        }
            predicateList.add(criteriaBuilder.between(root.get("price"), productSearchCriteria.getMinPrice(), productSearchCriteria.getMaxPrice()));

        return criteriaBuilder.and(predicateList.toArray(new Predicate[0]));
    }
    //find products inside brands, find products inside product_type, find products inside price min and max
    public List<Product> findProductsWithFilters(List<String> brands, List<String> product_type, double minPrice, double maxPrice){
        CriteriaQuery<Product> criteriaQuery = criteriaBuilder.createQuery(Product.class); //Preparing to make query
        Root<Product> root = criteriaQuery.from(Product.class);
        List<Predicate> predicateList = new ArrayList<>();

        if(brands != null){
            predicateList.add(root.get("brand").in(brands));
        }
        if(product_type != null){
            predicateList.add(root.get("type").in(product_type));
        }
        predicateList.add(criteriaBuilder.between(root.get("price"), minPrice, maxPrice));

        Predicate predicate = criteriaBuilder.and(predicateList.toArray(new Predicate[0]));
        criteriaQuery.where(predicate);
        Query query = entityManager.createQuery(criteriaQuery);

        List<Product> products = query.getResultList();
        return  products;
    }

    public long getProductCount(Predicate predicate, ProductSearchCriteria productSearchCriteria){

        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<Product> root = countQuery.from(Product.class);
        Predicate p = getPredicate(root, productSearchCriteria);

        countQuery.select(criteriaBuilder.count(root)).where(p);
        TypedQuery<Long> typed =entityManager.createQuery(countQuery);
        System.out.println("LENGTH: " + typed.getSingleResult());
        return typed.getSingleResult();


//        return entityManager.createQuery(countQuery).getSingleResult();
//        return 10;

    }
}
