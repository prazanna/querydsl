package com.mysema.query.collections;

import static org.junit.Assert.*;

import org.junit.Test;

public class CastTest extends AbstractQueryTest {
    
    @Test
    public void Parents() {
        QCat cat = QAnimal.animal.as(QCat.class);
        assertEquals(QAnimal.animal, cat.getMetadata().getParent());
    }
    
    @Test
    public void Cast() {
        query().from(QAnimal.animal, cats)
            .where(QAnimal.animal.as(QCat.class).breed.eq(0))
            .list(QAnimal.animal);
    }

    @Test
    public void PropertyDereference() {
         Cat cat = new Cat();
         cat.setEyecolor(Color.TABBY);
         assertEquals(Color.TABBY, 
             CollQueryFactory.from(QAnimal.animal, cat)
                 .where(QAnimal.animal.instanceOf(Cat.class))
                 .singleResult(QAnimal.animal.as(QCat.class).eyecolor));
    }
    
}
