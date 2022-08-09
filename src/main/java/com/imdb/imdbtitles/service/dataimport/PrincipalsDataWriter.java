package com.imdb.imdbtitles.service.dataimport;

import com.imdb.imdbtitles.entity.Role;
import com.imdb.imdbtitles.repository.RoleRepository;
import com.imdb.imdbtitles.service.dataimport.common.DefaultBatchInsert;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static com.imdb.imdbtitles.util.ParseUtil.getInteger;

@Setter
@Log4j2
public class PrincipalsDataWriter extends DefaultBatchInsert {

    private static final String SQL = "INSERT INTO `Principal` (Media_Item_id, ordering, person_id, role_id) VALUES(?,?,?,?)";
    private RoleRepository roleRepository;

    private Set<Role> roleList;

    public PrincipalsDataWriter(JdbcTemplate jdbcTemplate, RoleRepository roleRepository) {
        super(jdbcTemplate, SQL);
        this.roleRepository = roleRepository;
        roleList = new HashSet<>(roleRepository.findAll());
    }

    @Override
    protected CompletableFuture<Void> runBatchInsertByThread(List<Object[]> lines) {
        return super.runBatchInsertByThread(lines);
    }

    @Override
    public Object[] parseLine(String line) {
        String[] fields = line.split("\t");
        Object[] values = new Object[]{fields[0], getInteger(fields[1]), fields[2], getRole(fields[3]).getId()};
        return values;
    }

    private synchronized Role getRole(String roleName) {
        return roleList.parallelStream().filter(role -> role.getName().equals(roleName)).findFirst().orElseGet(() -> {
            Role role = Role.builder().name(roleName).build();
            role = roleRepository.saveAndFlush(role);
            roleList.add(role);
            return role;
        });
    }

}
