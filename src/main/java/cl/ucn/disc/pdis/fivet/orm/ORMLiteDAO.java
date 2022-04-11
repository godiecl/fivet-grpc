/*
 * Copyright (c) 2022 Diego Urrutia-Astorga.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to
 * deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 *
 */

package cl.ucn.disc.pdis.fivet.orm;

import cl.ucn.disc.pdis.fivet.model.BaseEntity;
import cl.ucn.disc.pdis.fivet.model.DAO;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * The ORMLite DAO implementation.
 *
 * @param <T> to parametrize.
 * @author Diego Urrutia-Astorga.
 */
public final class ORMLiteDAO<T extends BaseEntity> implements DAO<T> {

    /**
     * The connection to ORMLite DAO.
     */
    private final Dao<T, Integer> theDao;

    /**
     * The Contructor.
     *
     * @param connectionSource to use.
     * @param clazz            to use.
     */
    @SneakyThrows(SQLException.class)
    public ORMLiteDAO(@NonNull ConnectionSource connectionSource, @NonNull Class<T> clazz) {
        this.theDao = DaoManager.createDao(connectionSource, clazz);
    }

    /**
     * Get optional T.
     *
     * @param id to retrieve.
     */
    @SneakyThrows(SQLException.class)
    @Override
    public Optional<T> get(@NonNull Integer id) {
        // Exec the query
        T t = this.theDao.queryForId(id);
        // Return the value
        return t == null ? Optional.empty() : Optional.of(t);
    }

    /**
     * Get all the T.
     */
    @SneakyThrows(SQLException.class)
    @Override
    public List<T> getAll() {
        return this.theDao.queryForAll();
    }

    /**
     * Save T into the  T.
     *
     * @param t to save.
     */
    @SneakyThrows(SQLException.class)
    @Override
    public void save(T t) {
        int created = this.theDao.create(t);
        if (created != 1) {
            throw new SQLException("Rows created != 1 !!");
        }
    }

    /**
     * Delete a T.
     *
     * @param t to delete.
     */
    @Override
    public void delete(T t) {
        this.delete(t.getId());
    }

    /**
     * Delete a T.
     *
     * @param id to delete.
     */
    @SneakyThrows(SQLException.class)
    @Override
    public void delete(Integer id) {
        int created = this.theDao.deleteById(id);
        if (created != 1) {
            throw new SQLException("Rows deleted != 1 !!");
        }
    }

}
